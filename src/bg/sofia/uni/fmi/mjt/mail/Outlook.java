package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.comparators.RuleComparator;
import bg.sofia.uni.fmi.mjt.mail.exceptions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Outlook implements MailClient {
    static final int MAX_PRIORITY = 1;
    static final int MIN_PRIORITY = 10;
    Map<String, Map<String, List<Mail>>> listOfAccounts;
    Map<String, List<Rule>> listOfRules;
    Map<String, String> listOfEmails;

    public Outlook() {
        listOfAccounts = new HashMap<>();
        listOfRules = new HashMap<>();
        listOfEmails = new HashMap<>();
    }

    @Override
    public Account addNewAccount(String accountName, String email) {
        addNewAccountValidation(accountName, email);

        Account newAccount = new Account(accountName, email);

        listOfAccounts.put(accountName, new HashMap<>());
        listOfRules.put(accountName, new ArrayList<>());
        listOfEmails.put(email, accountName);

        listOfAccounts.get(accountName).put("inbox", new ArrayList<>());
        listOfAccounts.get(accountName).put("sent", new ArrayList<>());

        return newAccount;
    }

    @Override
    public void createFolder(String accountName, String path) {
        createFolderValidation(accountName, path);

        listOfAccounts.get(accountName).put(path, new ArrayList<>());
    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        addRuleValidation(accountName, folderPath, ruleDefinition, priority);

        Rule newRule = createRule(accountName, folderPath, ruleDefinition, priority);

        duplicateRulesCheck(accountName, newRule);

        listOfRules.get(accountName).add(newRule);
        listOfRules.get(accountName).sort(new RuleComparator());

        listOfAccounts.put(accountName, applyNewRule(accountName));
    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {
        receiveMailValidation(accountName, mailMetadata, mailContent);

        Mail newMail = createMail(accountName, mailMetadata, mailContent);

        for (Rule rule : listOfRules.get(accountName)) {
            if (checkKeywordsMetadata(rule, mailMetadata, "subject-includes:") ||
                checkKeywordsMetadata(rule, mailMetadata, "subject-or-body-includes:") ||
                checkKeywordsMetadata(rule, mailMetadata, "recipients-includes:") ||
                checkKeywordsMetadata(rule, mailMetadata, "sender:") ||
                checkKeywordsContent(rule, mailContent)) {

                listOfAccounts.get(accountName).get(rule.folderPath()).add(newMail);
                return;
            }
        }

        listOfAccounts.get(accountName).get("inbox").add(newMail);
    }

    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {
        getMailsFromFolderValidation(account, folderPath);

        return new ArrayList<>(listOfAccounts.get(account).get(folderPath));
    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {
        sendMailValidation(accountName, mailMetadata, mailContent);

        Mail newMail = createMail(accountName, mailMetadata, mailContent);

        listOfAccounts.get(accountName).get("sent").add(newMail);

        sendMailsToRecipients(newMail.recipients(), mailMetadata, mailContent);
    }

    private void addNewAccountValidation(String accountName, String email) throws
        IllegalArgumentException, AccountAlreadyExistsException {
        if (accountName == null || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        if (listOfAccounts.containsKey(accountName)) {
            throw new AccountAlreadyExistsException("Account with that account name already exists");
        }
        if (listOfEmails.containsKey(email)) {
            throw new AccountAlreadyExistsException("Account with that email already exists");
        }
    }

    private void createFolderValidation(String accountName, String path) throws
        IllegalArgumentException, AccountNotFoundException, InvalidPathException, FolderAlreadyExistsException {
        if (accountName == null || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name cannot be null or blank");
        }
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path cannot be null or blank");
        }

        if (!listOfAccounts.containsKey(accountName)) {
            throw new AccountNotFoundException("Account doesn't exist");
        }

        if (listOfAccounts.get(accountName).containsKey(path)) {
            throw new FolderAlreadyExistsException("Folder already exists");
        }

        if (!path.startsWith("inbox")) {
            throw new InvalidPathException("Path doesn't start with root");
        }

        int lastSeparatorIndex = path.lastIndexOf("/");
        if (!listOfAccounts.get(accountName).containsKey(path.substring(0, lastSeparatorIndex))) {
            throw new InvalidPathException("Some intermediate folders do not exist");
        }
    }

    private void addRuleValidation(String accountName, String folderPath, String ruleDefinition, int priority) throws
        IllegalArgumentException, AccountNotFoundException, FolderNotFoundException {
        if (accountName == null || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name cannot be null or blank");
        }
        if (folderPath == null || folderPath.isBlank()) {
            throw new IllegalArgumentException("Folder path cannot be null or blank");
        }
        if (ruleDefinition == null || ruleDefinition.isBlank()) {
            throw new IllegalArgumentException("Rule definition cannot be null or blank");
        }
        if (priority < MAX_PRIORITY || priority > MIN_PRIORITY) {
            throw new IllegalArgumentException("Priority should be within 1 and 10");
        }

        if (!listOfAccounts.containsKey(accountName)) {
            throw new AccountNotFoundException("Account doesn't exist");
        }

        if (!listOfAccounts.get(accountName).containsKey(folderPath)) {
            throw new FolderNotFoundException("Folder do not exist");
        }
    }

    private void receiveMailValidation(String accountName, String mailMetadata, String mailContent) throws
        IllegalArgumentException, AccountNotFoundException, FolderNotFoundException {
        if (accountName == null || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name cannot be null or blank");
        }
        if (mailMetadata == null || mailMetadata.isBlank()) {
            throw new IllegalArgumentException("Mail metadata cannot be null or blank");
        }
        if (mailContent == null || mailContent.isBlank()) {
            throw new IllegalArgumentException("Mail content cannot be null or blank");
        }

        if (!listOfAccounts.containsKey(accountName)) {
            throw new AccountNotFoundException("Account doesn't exist");
        }
    }

    private void getMailsFromFolderValidation(String account, String folderPath) throws
        IllegalArgumentException, AccountNotFoundException, FolderNotFoundException {
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("Account name cannot be null or blank");
        }
        if (folderPath == null || folderPath.isBlank()) {
            throw new IllegalArgumentException("Folder path cannot be null or blank");
        }

        if (!listOfAccounts.containsKey(account)) {
            throw new AccountNotFoundException("Account doesn't exist");
        }

        if (!listOfAccounts.get(account).containsKey(folderPath)) {
            throw new FolderNotFoundException("Folder path doesn't exist");
        }
    }

    private void sendMailValidation(String accountName, String mailMetadata, String mailContent) throws
        IllegalArgumentException {
        if (accountName == null || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name cannot be null or blank");
        }
        if (mailMetadata == null || mailMetadata.isBlank()) {
            throw new IllegalArgumentException("Mail metadata cannot be null or blank");
        }
        if (mailContent == null || mailContent.isBlank()) {
            throw new IllegalArgumentException("Mail content cannot be null or blank");
        }
    }

    private void duplicateRulesCheck(String accountName, Rule newRule) throws RuleAlreadyDefinedException {
        for (Rule rule : listOfRules.get(accountName)) {
            if (rule.priority() == newRule.priority()) {
                if (!Collections.disjoint(rule.subjectKeywords(), newRule.subjectKeywords()) ||
                    !Collections.disjoint(rule.subjectOrBodyKeywords(), newRule.subjectOrBodyKeywords()) ||
                    !Collections.disjoint(rule.recipients(), newRule.recipients()) ||
                    rule.sender().equals(newRule.sender())) {
                    throw new RuleAlreadyDefinedException("Rule is already defined");
                }
            }
        }
    }

    private HashSet<String> extractKeywords(String ruleDefinition, String tag) {
        HashSet<String> keywords = new HashSet<>();

        Scanner scanner = new Scanner(ruleDefinition);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith(tag)) {
                line = line.replace(tag, "");

                List<String> keywordsUnformatted = List.of(line.split(","));
                for (String keyword : keywordsUnformatted) {
                    keywords.add(keyword.strip());
                }

                break;
            }
        }
        scanner.close();

        return keywords;
    }

    private String extractText(String ruleDefinition, String tag) {
        String text = null;

        Scanner scanner = new Scanner(ruleDefinition);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith(tag)) {
                line = line.replace(tag, "");

                text = line.strip();

                break;
            }
        }
        scanner.close();

        return text;
    }

    private Rule createRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        Set<String> subjectKeywords = extractKeywords(ruleDefinition, "subject-includes:");
        Set<String> subjectOrBodyKeywords = extractKeywords(ruleDefinition, "subject-or-body-includes:");
        Set<String> recipients = extractKeywords(ruleDefinition, "recipients-includes:");
        Set<String> sender = extractKeywords(ruleDefinition, "from:");

        return new Rule(accountName, folderPath, priority,
            subjectKeywords, subjectOrBodyKeywords, recipients, sender);
    }

    private Mail createMail(String accountName, String mailMetadata, String mailContent) {
        String senderEmail = extractText(mailMetadata, "sender:");
        if (!listOfEmails.containsKey(senderEmail)) {
            String senderEmailCorrect = findCorrectSender(accountName);
            mailMetadata = mailMetadata.replace("sender: " + senderEmail, "sender: " + senderEmailCorrect);
            senderEmail = senderEmailCorrect;
        }

        Account sender = new Account(senderEmail, listOfEmails.get(senderEmail));

        String subject = extractText(mailMetadata, "subject:");
        Set<String> recipients = extractKeywords(mailMetadata, "recipients:");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(extractText(mailMetadata, "received:"), formatter);

        return new Mail(sender, recipients, subject, mailContent, dateTime);
    }

    private boolean checkKeywordsMetadata(Rule rule, String mailMetadata, String tag) {
        switch (tag) {
            case "subject-includes:": {
                for (String keyword : rule.subjectKeywords()) {
                    if (mailMetadata.contains(keyword)) {
                        return true;
                    }
                }

                return false;
            }
            case "subject-or-body-includes:": {
                for (String keyword : rule.subjectOrBodyKeywords()) {
                    if (mailMetadata.contains(keyword)) {
                        return true;
                    }
                }

                return false;
            }
            case "recipients-includes:": {
                Set<String> keywords = extractKeywords(mailMetadata, tag);
                return !keywords.isEmpty() && !Collections.disjoint(keywords, rule.recipients());
            }
            case "sender:": {
                Set<String> keywords = extractKeywords(mailMetadata, tag);
                return !keywords.isEmpty() && !Collections.disjoint(keywords, rule.sender());
            }
            default:
                return false;
        }
    }

    private boolean checkKeywordsContent(Rule rule, String mailContent) {
        for (String keyword : rule.subjectOrBodyKeywords()) {
            if (mailContent.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private Map<String, List<Mail>> applyNewRule(String account) {
        Map<String, List<Mail>> mailRearrangement = new HashMap<>();

        for (String folder : listOfAccounts.get(account).keySet()) {
            mailRearrangement.put(folder, new ArrayList<>());
        }

        for (String folder : listOfAccounts.get(account).keySet()) {
            for (Mail mail : listOfAccounts.get(account).get(folder)) {
                if (folder.startsWith("sent")) {
                    mailRearrangement.get("sent").add(mail);
                    continue;
                }

                String path = findPath(listOfRules.get(account), mail);

                mailRearrangement.get(Objects.requireNonNullElse(path, "inbox")).add(mail);
            }
        }

        return mailRearrangement;
    }

    private String findPath(List<Rule> rules, Mail mail) {
        for (Rule rule : rules) {
            for (String keyword : rule.subjectKeywords()) {
                if (mail.subject().contains(keyword)) {
                    return rule.folderPath();
                }
            }

            for (String keyword : rule.subjectOrBodyKeywords()) {
                if (mail.subject().contains(keyword) || mail.body().contains(keyword)) {
                    return rule.folderPath();
                }
            }

            for (String recipient : rule.recipients()) {
                if (mail.recipients().contains(recipient)) {
                    return rule.folderPath();
                }
            }

            for (String sender : rule.sender()) {
                if (mail.sender().name().contains(sender)) {
                    return rule.folderPath();
                }
            }
        }

        return null;
    }

    private String findCorrectSender(String accountName) {
        String senderEmailCorrect = null;

        for (String email : listOfEmails.keySet()) {
            if (listOfEmails.get(email).equals(accountName)) {
                senderEmailCorrect = email;
                break;
            }
        }

        return senderEmailCorrect;
    }

    private void sendMailsToRecipients(Set<String> recipients, String mailMetadata, String mailContent) {
        for (String recipient : recipients) {
            if (listOfEmails.containsKey(recipient)) {
                receiveMail(listOfEmails.get(recipient), mailMetadata, mailContent);
            }
        }
    }
}
