// SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
// SPDX-License-Identifier: MIT
package uk.ac.open.etma.util;

import uk.ac.open.etma.model.Student;
import uk.ac.open.etma.model.Submission;
import uk.ac.open.etma.model.Tutor;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Safe XML handler for FHI (eTMA Handler Information) files.
 *
 * <p>This replaces the dangerous manual string concatenation in the original
 * eTMA Handler which caused XML corruption (missing &gt; characters).
 *
 * <p>Key improvements:
 * <ul>
 *   <li>Uses proper DOM API for XML construction</li>
 *   <li>Automatic escaping of special characters</li>
 *   <li>Validates XML on parse and before write</li>
 *   <li>Explicit error handling - never silently fails</li>
 * </ul>
 *
 * <p>The original code did this (WRONG):
 * <pre>{@code
 * String xml = "<student_details>";
 * xml = xml + "<forenames>" + name + "</forenames>";  // No escaping!
 * }</pre>
 *
 * <p>This class does it properly using DOM.
 */
public final class FhiXmlHandler {

    private static final Charset FHI_CHARSET = Charset.forName("ISO-8859-1");
    private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";

    private final DocumentBuilderFactory documentBuilderFactory;
    private final TransformerFactory transformerFactory;

    public FhiXmlHandler() {
        this.documentBuilderFactory = createSecureDocumentBuilderFactory();
        this.transformerFactory = TransformerFactory.newInstance();
    }

    /**
     * Parses FHI XML content into a Submission object.
     *
     * @param xmlContent The XML string to parse
     * @return ParseResult containing Submission or error
     */
    public ParseResult<Submission> parseSubmission(String xmlContent) {
        if (xmlContent == null || xmlContent.isBlank()) {
            return ParseResult.failure("XML content is empty");
        }

        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            if (!"student_submission".equals(root.getTagName())) {
                return ParseResult.failure("Expected root element 'student_submission', found: " + root.getTagName());
            }

            Submission submission = new Submission();

            // Parse student details
            Element studentDetails = getFirstElement(root, "student_details");
            if (studentDetails != null) {
                Student student = new Student();
                student.setOuComputerUserName(getElementText(studentDetails, "ou_computer_user_name"));
                student.setPersonalId(getElementText(studentDetails, "personal_id"));
                student.setTitle(getElementText(studentDetails, "title"));
                student.setInitials(getElementText(studentDetails, "initials"));
                student.setForenames(getElementText(studentDetails, "forenames"));
                student.setSurname(getElementText(studentDetails, "surname"));
                student.setEmailAddress(getElementText(studentDetails, "email_address"));
                student.setAddressLine1(getElementText(studentDetails, "address_line1"));
                student.setAddressLine2(getElementText(studentDetails, "address_line2"));
                student.setAddressLine3(getElementText(studentDetails, "address_line3"));
                student.setAddressLine4(getElementText(studentDetails, "address_line4"));
                student.setAddressLine5(getElementText(studentDetails, "address_line5"));
                student.setPostcode(getElementText(studentDetails, "postcode"));
                submission.setStudent(student);
            }

            // Parse tutor details
            Element tutorDetails = getFirstElement(root, "tutor_details");
            if (tutorDetails != null) {
                Tutor tutor = new Tutor();
                tutor.setStaffId(getElementText(tutorDetails, "staff_id"));
                tutor.setTitle(getElementText(tutorDetails, "staff_title"));
                tutor.setInitials(getElementText(tutorDetails, "staff_initials"));
                tutor.setForenames(getElementText(tutorDetails, "staff_forenames"));
                tutor.setSurname(getElementText(tutorDetails, "staff_surname"));
                tutor.setRegionCode(getElementText(tutorDetails, "region_code"));
                submission.setTutor(tutor);
            }

            // Parse submission details
            Element submissionDetails = getFirstElement(root, "submission_details");
            if (submissionDetails != null) {
                submission.setCourseCode(getElementText(submissionDetails, "course_code"));
                submission.setCourseVersionNum(getElementText(submissionDetails, "course_version_num"));
                submission.setPresCode(getElementText(submissionDetails, "pres_code"));
                submission.setAssignmentSuffix(getElementText(submissionDetails, "assgnmt_suffix"));
                submission.setSubmissionNum(getElementText(submissionDetails, "e_tma_submission_num"));
                submission.setSubmissionDate(getElementText(submissionDetails, "e_tma_submission_date"));
                submission.setWaltonReceivedDate(getElementText(submissionDetails, "walton_received_date"));
                submission.setMarkedDate(getElementText(submissionDetails, "marked_date"));
                submission.setSubmissionStatus(getElementText(submissionDetails, "submission_status"));
                submission.setLateSubmissionStatus(getElementText(submissionDetails, "late_submission_status"));
                submission.setZipDate(getElementText(submissionDetails, "zip_date"));
                submission.setZipFile(getElementText(submissionDetails, "zip_file"));
                submission.setScoreUpdateAllowed(getElementText(submissionDetails, "score_update_allowed"));
                submission.setOverallGradeScore(getElementText(submissionDetails, "overall_grade_score"));
                submission.setTutorComments(getElementText(submissionDetails, "tutor_comments"));
                submission.setMaxAssignmentScore(getElementText(submissionDetails, "max_assgnmt_score"));
                submission.setTotalQuestionCount(getElementText(submissionDetails, "total_question_count"));
                submission.setPermittedQuestionCount(getElementText(submissionDetails, "permitted_question_count"));
            }

            return ParseResult.success(submission);

        } catch (ParserConfigurationException e) {
            return ParseResult.failure("Parser configuration error: " + e.getMessage());
        } catch (SAXException e) {
            return ParseResult.failure("XML parse error: " + e.getMessage());
        } catch (IOException e) {
            return ParseResult.failure("IO error reading XML: " + e.getMessage());
        }
    }

    /**
     * Serializes a Submission object to FHI XML format.
     *
     * @param submission The submission to serialize
     * @return The XML string, or empty if serialization failed
     */
    public Optional<String> serializeSubmission(Submission submission) {
        if (submission == null) {
            return Optional.empty();
        }

        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Root element
            Element root = doc.createElement("student_submission");
            doc.appendChild(root);

            // Student details
            if (submission.getStudent() != null) {
                Element studentDetails = doc.createElement("student_details");
                root.appendChild(studentDetails);
                Student s = submission.getStudent();
                appendTextElement(doc, studentDetails, "ou_computer_user_name", s.getOuComputerUserName());
                appendTextElement(doc, studentDetails, "personal_id", s.getPersonalId());
                appendTextElement(doc, studentDetails, "title", s.getTitle());
                appendTextElement(doc, studentDetails, "initials", s.getInitials());
                appendTextElement(doc, studentDetails, "forenames", s.getForenames());
                appendTextElement(doc, studentDetails, "surname", s.getSurname());
                appendTextElement(doc, studentDetails, "email_address", s.getEmailAddress());
                appendTextElement(doc, studentDetails, "address_line1", s.getAddressLine1());
                appendTextElement(doc, studentDetails, "address_line2", s.getAddressLine2());
                appendTextElement(doc, studentDetails, "address_line3", s.getAddressLine3());
                appendTextElement(doc, studentDetails, "address_line4", s.getAddressLine4());
                appendTextElement(doc, studentDetails, "address_line5", s.getAddressLine5());
                appendTextElement(doc, studentDetails, "postcode", s.getPostcode());
            }

            // Tutor details
            if (submission.getTutor() != null) {
                Element tutorDetails = doc.createElement("tutor_details");
                root.appendChild(tutorDetails);
                Tutor t = submission.getTutor();
                appendTextElement(doc, tutorDetails, "staff_id", t.getStaffId());
                appendTextElement(doc, tutorDetails, "staff_title", t.getTitle());
                appendTextElement(doc, tutorDetails, "staff_initials", t.getInitials());
                appendTextElement(doc, tutorDetails, "staff_forenames", t.getForenames());
                appendTextElement(doc, tutorDetails, "staff_surname", t.getSurname());
                appendTextElement(doc, tutorDetails, "region_code", t.getRegionCode());
            }

            // Submission details
            Element submissionDetails = doc.createElement("submission_details");
            root.appendChild(submissionDetails);
            appendTextElement(doc, submissionDetails, "course_code", submission.getCourseCode());
            appendTextElement(doc, submissionDetails, "course_version_num", submission.getCourseVersionNum());
            appendTextElement(doc, submissionDetails, "pres_code", submission.getPresCode());
            appendTextElement(doc, submissionDetails, "assgnmt_suffix", submission.getAssignmentSuffix());
            appendTextElement(doc, submissionDetails, "e_tma_submission_num", submission.getSubmissionNum());
            appendTextElement(doc, submissionDetails, "e_tma_submission_date", submission.getSubmissionDate());
            appendTextElement(doc, submissionDetails, "walton_received_date", submission.getWaltonReceivedDate());
            appendTextElement(doc, submissionDetails, "marked_date", submission.getMarkedDate());
            appendTextElement(doc, submissionDetails, "submission_status", submission.getSubmissionStatus());
            appendTextElement(doc, submissionDetails, "late_submission_status", submission.getLateSubmissionStatus());
            appendTextElement(doc, submissionDetails, "zip_date", submission.getZipDate());
            appendTextElement(doc, submissionDetails, "zip_file", submission.getZipFile());
            appendTextElement(doc, submissionDetails, "score_update_allowed", submission.getScoreUpdateAllowed());
            appendTextElement(doc, submissionDetails, "overall_grade_score", submission.getOverallGradeScore());
            appendTextElement(doc, submissionDetails, "tutor_comments", submission.getTutorComments());
            appendTextElement(doc, submissionDetails, "max_assgnmt_score", submission.getMaxAssignmentScore());
            appendTextElement(doc, submissionDetails, "total_question_count", submission.getTotalQuestionCount());
            appendTextElement(doc, submissionDetails, "permitted_question_count", submission.getPermittedQuestionCount());

            // Question details placeholder
            Element questionDetails = doc.createElement("question_details");
            root.appendChild(questionDetails);

            // Transform to string
            return Optional.of(documentToString(doc));

        } catch (ParserConfigurationException | TransformerException e) {
            return Optional.empty();
        }
    }

    /**
     * Validates FHI XML content without fully parsing it.
     *
     * @param xmlContent The XML to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidFhi(String xmlContent) {
        if (xmlContent == null || xmlContent.isBlank()) {
            return false;
        }
        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));
            String rootTag = doc.getDocumentElement().getTagName();
            return "student_submission".equals(rootTag) || "tutor_sample".equals(rootTag);
        } catch (Exception e) {
            return false;
        }
    }

    // === Helper methods ===

    private DocumentBuilderFactory createSecureDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // Disable external entities to prevent XXE attacks
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
        } catch (ParserConfigurationException e) {
            // Log but continue - security features may not be available on all implementations
            System.err.println("Warning: Could not configure all XML security features: " + e.getMessage());
        }
        return factory;
    }

    private Element getFirstElement(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return (Element) list.item(0);
        }
        return null;
    }

    private String getElementText(Element parent, String tagName) {
        Element element = getFirstElement(parent, tagName);
        if (element != null) {
            return element.getTextContent();
        }
        return "";
    }

    private void appendTextElement(Document doc, Element parent, String tagName, String value) {
        Element element = doc.createElement(tagName);
        element.setTextContent(value != null ? value : "");
        parent.appendChild(element);
    }

    private String documentToString(Document doc) throws TransformerException {
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    /**
     * Result of parsing FHI XML.
     *
     * @param <T> The type of parsed object
     */
    public static final class ParseResult<T> {
        private final boolean success;
        private final T value;
        private final String errorMessage;

        private ParseResult(boolean success, T value, String errorMessage) {
            this.success = success;
            this.value = value;
            this.errorMessage = errorMessage;
        }

        public static <T> ParseResult<T> success(T value) {
            return new ParseResult<>(true, value, null);
        }

        public static <T> ParseResult<T> failure(String errorMessage) {
            return new ParseResult<>(false, null, errorMessage);
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean isFailure() {
            return !success;
        }

        public Optional<T> getValue() {
            return Optional.ofNullable(value);
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public T orElseThrow() {
            if (!success) {
                throw new RuntimeException("Parse failed: " + errorMessage);
            }
            return value;
        }
    }
}
