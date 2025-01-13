package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class OutlookTest {

    @Test
    void testAddNewAccountInvalidAccountName() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
            () -> outlook.addNewAccount(null, "testy@gmail.com"),
            "IllegalArgumentException should be thrown for invalid account name");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addNewAccount(" ", "testy@gmail.com"),
            "IllegalArgumentException should be thrown for invalid account name");
    }

    @Test
    void testAddNewAccountInvalidEmail() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
            () -> outlook.addNewAccount("test", null),
            "IllegalArgumentException should be thrown for invalid email");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addNewAccount("test", " "),
            "IllegalArgumentException should be thrown for invalid email");
    }

    @Test
    void testAddNewAccountAlreadyExists() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");

        assertThrows(AccountAlreadyExistsException.class,
            () -> outlook.addNewAccount("test", "unique"),
            "AccountAlreadyExistsException should be thrown if account name already exists");
        assertThrows(AccountAlreadyExistsException.class,
            () -> outlook.addNewAccount("unique", "testy@gmail.com"),
            "AccountAlreadyExistsException should be thrown if email already exists");
    }

    @Test
    void testCreateFolderInvalidAccountName() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
            () -> outlook.createFolder(null, "inbox/test"),
            "IllegalArgumentException should be thrown for invalid account name");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.createFolder(" ", "inbox/test"),
            "IllegalArgumentException should be thrown for invalid account name");
    }

    @Test
    void testCreateFolderInvalidPath() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
            () -> outlook.createFolder("test", null),
            "IllegalArgumentException should be thrown for invalid path");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.createFolder("test", " "),
            "IllegalArgumentException should be thrown for invalid path");
    }

    @Test
    void testCreateFolderAccountNotFound() {
        Outlook outlook = new Outlook();

        assertThrows(AccountNotFoundException.class,
            () -> outlook.createFolder("test", "inbox/test"),
            "AccountNotFoundException should be thrown if the account is not present");
    }

    @Test
    void testCreateFolderDoesNotStartFromRoot() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");

        assertThrows(InvalidPathException.class,
            () -> outlook.createFolder("test", "notInbox"),
            "InvalidPathException should be thrown if path does not start from the root folder of received mails");
    }

    @Test
    void testCreateFolderIntermediateFolderDoesNotExist() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");

        assertThrows(InvalidPathException.class,
            () -> outlook.createFolder("test", "inbox/folder1/folder2"),
            "InvalidPathException should be thrown if if some intermediate folders do not exist");
    }

    @Test
    void testCreateFolderAlreadyExists() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");
        outlook.createFolder("test", "inbox/test");

        assertThrows(FolderAlreadyExistsException.class,
            () -> outlook.createFolder("test", "inbox/test"),
            "FolderAlreadyExistsException should be thrown if the absolute path is already present");
    }

    @Test
    void testAddRuleInvalidAccountName() {
        Outlook outlook = new Outlook();

        String ruleDefinition =
            "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "recipients-includes: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator() +
                "from: stoyo@fmi.bg";

        assertThrows(IllegalArgumentException.class,
            () -> outlook.addRule(null, "inbox", ruleDefinition, 1),
            "IllegalArgumentException should be thrown for invalid account name");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addRule(" ", "inbox", ruleDefinition, 1),
            "IllegalArgumentException should be thrown for invalid account name");
    }

    @Test
    void testAddRuleInvalidFolderPath() {
        Outlook outlook = new Outlook();

        String ruleDefinition =
            "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "recipients-includes: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator() +
                "from: stoyo@fmi.bg";

        assertThrows(IllegalArgumentException.class,
            () -> outlook.addRule("test", null, ruleDefinition, 1),
            "IllegalArgumentException should be thrown for invalid folder path");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addRule("test", " ", ruleDefinition, 1),
            "IllegalArgumentException should be thrown for invalid folder path");
    }

    @Test
    void testAddRuleInvalidRuleDefinition() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
            () -> outlook.addRule("test", "inbox", null, 1),
            "IllegalArgumentException should be thrown for invalid rule definition");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addRule("test", "inbox", " ", 1),
            "IllegalArgumentException should be thrown for invalid rule definition");
    }

    @Test
    void testAddRuleInvalidPriority() {
        Outlook outlook = new Outlook();

        String ruleDefinition =
            "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "recipients-includes: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator() +
                "from: stoyo@fmi.bg";

        assertThrows(IllegalArgumentException.class,
            () -> outlook.addRule("test", "inbox", ruleDefinition, -1),
            "IllegalArgumentException should be thrown if priority is out of [1,10]");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.addRule("test", "inbox", ruleDefinition, 13),
            "IllegalArgumentException should be thrown if priority is out of [1,10]");
    }

    @Test
    void testAddRuleAccountNotFound() {
        Outlook outlook = new Outlook();

        String ruleDefinition =
            "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "recipients-includes: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator() +
                "from: stoyo@fmi.bg";

        assertThrows(AccountNotFoundException.class,
            () -> outlook.addRule("test", "inbox", ruleDefinition, 1),
            "AccountNotFoundException should be thrown if the account is not present");
    }

    @Test
    void testAddRuleFolderNotFound() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");

        String ruleDefinition =
            "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "recipients-includes: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator() +
                "from: stoyo@fmi.bg";

        assertThrows(FolderNotFoundException.class,
            () -> outlook.addRule("test", "inbox/test", ruleDefinition, 1),
            "FolderNotFoundException should be thrown for invalid folder path");
    }

    @Test
    void testAddRuleRuleAlreadyDefined() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");
        outlook.createFolder("test", "inbox/test");

        String ruleDefinition =
            "subject-includes: mjt, izpit, 2022" + System.lineSeparator() +
                "subject-or-body-includes: izpit" + System.lineSeparator() +
                "recipients-includes: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator() +
                "from: stoyo@fmi.bg";

        outlook.addRule("test", "inbox/test", ruleDefinition, 1);

        assertThrows(RuleAlreadyDefinedException.class,
            () -> outlook.addRule("test", "inbox/test", ruleDefinition, 1),
            "RuleAlreadyDefinedException should be thrown if the rule definition contains a rule/condition that already exists");
    }

    @Test
    void testGetMailsFromFolderInvalidAccountName() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
            () -> outlook.getMailsFromFolder(null, "inbox"),
            "IllegalArgumentException should be thrown for invalid account name");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.getMailsFromFolder(" ", "inbox"),
            "IllegalArgumentException should be thrown for invalid account name");
    }

    @Test
    void testGetMailsFromFolderInvalidPath() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
            () -> outlook.getMailsFromFolder("test", null),
            "IllegalArgumentException should be thrown for invalid path");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.getMailsFromFolder("test", " "),
            "IllegalArgumentException should be thrown for invalid path");
    }

    @Test
    void testGetMailsFromFolderAccountNotFound() {
        Outlook outlook = new Outlook();

        assertThrows(AccountNotFoundException.class,
            () -> outlook.getMailsFromFolder("test", "inbox/test"),
            "AccountNotFoundException should be thrown if the account is not present");
    }

    @Test
    void testGetMailsFromFolderFolderNotFound() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");

        assertThrows(FolderNotFoundException.class,
            () -> outlook.getMailsFromFolder("test", "inbox/test"),
            "FolderNotFoundException should be thrown for invalid folder path");
    }

    @Test
    void testReceiveMailInvalidAccountName() {
        Outlook outlook = new Outlook();

        String metadata =
            "sender: testy@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        assertThrows(IllegalArgumentException.class,
            () -> outlook.receiveMail(null, metadata, "lorem ipsum"),
            "IllegalArgumentException should be thrown for invalid account name");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.receiveMail(" ", metadata, "lorem ipsum"),
            "IllegalArgumentException should be thrown for invalid account name");
    }

    @Test
    void testReceiveMailInvalidMailMetadata() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
            () -> outlook.receiveMail("test", null, "lorem ipsum"),
            "IllegalArgumentException should be thrown for invalid mail metadata");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.receiveMail("test", " ", "lorem ipsum"),
            "IllegalArgumentException should be thrown for invalid mail metadata");
    }

    @Test
    void testReceiveMailInvalidMailContent() {
        Outlook outlook = new Outlook();

        String metadata =
            "sender: testy@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        assertThrows(IllegalArgumentException.class,
            () -> outlook.receiveMail("test", metadata, null),
            "IllegalArgumentException should be thrown for invalid mail content");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.receiveMail("test", metadata, " "),
            "IllegalArgumentException should be thrown for invalid mail content");
    }

    @Test
    void testReceiveMailAccountNotFound() {
        Outlook outlook = new Outlook();

        String metadata =
            "sender: testy@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        assertThrows(AccountNotFoundException.class,
            () -> outlook.receiveMail("test", metadata, "lorem ipsum"),
            "AccountNotFoundException should be thrown if the account is not present");
    }

    @Test
    void testReceiveMailCorrectFolder() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");
        outlook.addNewAccount("stoyo", "stoyo@fmi.bg");
        outlook.createFolder("test", "inbox/important");

        String ruleDefinition = "from: stoyo@fmi.bg";

        outlook.addRule("test", "inbox/important", ruleDefinition, 1);

        String metadata =
            "sender: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients: gosho@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        outlook.receiveMail("test", metadata, "lorem ipsum");

        Account account = new Account("stoyo@fmi.bg", "stoyo");
        HashSet<String> recipients = new HashSet<>();
        recipients.add("gosho@gmail.com");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2022-12-08 14:14", formatter);

        Collection<Mail> expected = new ArrayList<>();
        expected.add(new Mail(account, recipients, "Hello, MJT!", "lorem ipsum", dateTime));

        assertArrayEquals(expected.toArray(),
            outlook.getMailsFromFolder("test", "inbox/important").toArray(),
            "Received mail should in the correct folder but is not");
    }

    @Test
    void testReceiveMailCorrectSeparateFolders() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");
        outlook.addNewAccount("stoyo", "stoyo@fmi.bg");
        outlook.addNewAccount("gosho", "gosho@gmail.com");
        outlook.createFolder("test", "inbox/important");

        String ruleDefinition = "from: stoyo@fmi.bg";

        outlook.addRule("test", "inbox/important", ruleDefinition, 1);

        HashSet<String> recipients = new HashSet<>();
        recipients.add("misho@gmail.com");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2022-12-08 14:14", formatter);

        String metadata1 =
            "sender: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients: misho@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        outlook.receiveMail("test", metadata1, "lorem ipsum");
        Account account1 = new Account("stoyo@fmi.bg", "stoyo");
        Collection<Mail> expectedImportant = new ArrayList<>();
        expectedImportant.add(new Mail(account1, recipients, "Hello, MJT!", "lorem ipsum", dateTime));

        String metadata2 =
            "sender: gosho@gmail.com" + System.lineSeparator() +
                "recipients: misho@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "received: 2022-12-08 14:14";
        outlook.receiveMail("test", metadata2, "lorem ipsum");
        Account account2 = new Account("gosho@gmail.com", "gosho");
        Collection<Mail> expected = new ArrayList<>();
        expected.add(new Mail(account2, recipients, "Hello, MJT!", "lorem ipsum", dateTime));

        assertArrayEquals(expectedImportant.toArray(),
            outlook.getMailsFromFolder("test", "inbox/important").toArray(),
            "Received mail should in the correct folder but is not");
        assertArrayEquals(expected.toArray(),
            outlook.getMailsFromFolder("test", "inbox").toArray(),
            "Received mail should in the correct folder but is not");
    }

    @Test
    void testReceiveMailCorrectFolderMultipleRules() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");
        outlook.addNewAccount("stoyo", "stoyo@fmi.bg");

        outlook.createFolder("test", "inbox/important");
        outlook.createFolder("test", "inbox/unimportant");

        String ruleDefinition = "subject-includes: Hello";
        outlook.addRule("test", "inbox/unimportant", ruleDefinition, 9);

        String ruleDefinitionImportant = "from: stoyo@fmi.bg";
        outlook.addRule("test", "inbox/important", ruleDefinitionImportant, 1);

        String metadata =
            "sender: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients: gosho@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        outlook.receiveMail("test", metadata, "lorem ipsum");

        Account account = new Account("stoyo@fmi.bg", "stoyo");
        HashSet<String> recipients = new HashSet<>();
        recipients.add("gosho@gmail.com");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2022-12-08 14:14", formatter);

        Collection<Mail> expected = new ArrayList<>();
        expected.add(new Mail(account, recipients, "Hello, MJT!", "lorem ipsum", dateTime));

        assertArrayEquals(expected.toArray(),
            outlook.getMailsFromFolder("test", "inbox/important").toArray(),
            "Received mail should in the correct folder but is not");
    }

    @Test
    void testReceiveMailCorrectFolderMultipleRulesHighestNotMet() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");
        outlook.addNewAccount("stoyo", "stoyo@fmi.bg");
        outlook.addNewAccount("gosho", "gosho@gmail.com");

        outlook.createFolder("test", "inbox/important");
        outlook.createFolder("test", "inbox/unimportant");

        String ruleDefinition = "subject-includes: Hello";
        outlook.addRule("test", "inbox/unimportant", ruleDefinition, 9);

        String ruleDefinitionImportant = "from: stoyo@fmi.bg";
        outlook.addRule("test", "inbox/important", ruleDefinitionImportant, 1);

        String metadata =
            "sender: gosho@gmail.com" + System.lineSeparator() +
                "recipients: stoyo@fmi.bg" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        outlook.receiveMail("test", metadata, "lorem ipsum");

        Account account = new Account("gosho@gmail.com", "gosho");
        HashSet<String> recipients = new HashSet<>();
        recipients.add("stoyo@fmi.bg");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2022-12-08 14:14", formatter);

        Collection<Mail> expected = new ArrayList<>();
        expected.add(new Mail(account, recipients, "Hello, MJT!", "lorem ipsum", dateTime));

        assertArrayEquals(expected.toArray(),
            outlook.getMailsFromFolder("test", "inbox/unimportant").toArray(),
            "Received mail should in the correct folder but is not");
    }

    @Test
    void testSendMailInvalidAccountName() {
        Outlook outlook = new Outlook();

        String metadata =
            "sender: testy@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        assertThrows(IllegalArgumentException.class,
            () -> outlook.sendMail(null, metadata, "lorem ipsum"),
            "IllegalArgumentException should be thrown for invalid account name");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.sendMail(" ", metadata, "lorem ipsum"),
            "IllegalArgumentException should be thrown for invalid account name");
    }

    @Test
    void testSendMailInvalidMailMetadata() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
            () -> outlook.sendMail("test", null, "lorem ipsum"),
            "IllegalArgumentException should be thrown for invalid mail metadata");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.sendMail("test", " ", "lorem ipsum"),
            "IllegalArgumentException should be thrown for invalid mail metadata");
    }

    @Test
    void testSendMailInvalidMailContent() {
        Outlook outlook = new Outlook();

        String metadata =
            "sender: testy@gmail.com" + System.lineSeparator() +
                "subject: Hello, MJT!" + System.lineSeparator() +
                "recipients: pesho@gmail.com, gosho@gmail.com" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        assertThrows(IllegalArgumentException.class,
            () -> outlook.sendMail("test", metadata, null),
            "IllegalArgumentException should be thrown for invalid mail content");
        assertThrows(IllegalArgumentException.class,
            () -> outlook.sendMail("test", metadata, " "),
            "IllegalArgumentException should be thrown for invalid mail content");
    }

    @Test
    void testSendMailSenderTagMissed() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");

        String metadata =
                "subject: Hello, MJT!" + System.lineSeparator() +
                "recipients: gosho@gmail.com" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        outlook.sendMail("test", metadata, "lorem ipsum");

        Account account = new Account("testy@gmail.com", "test");
        HashSet<String> recipients = new HashSet<>();
        recipients.add("gosho@gmail.com");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2022-12-08 14:14", formatter);

        Collection<Mail> expected = new ArrayList<>();
        expected.add(new Mail(account, recipients, "Hello, MJT!", "lorem ipsum", dateTime));

        assertArrayEquals(expected.toArray(),
            outlook.getMailsFromFolder("test", "sent").toArray(),
            "\"sender\" field should be included automatically if missing or not correctly set but it does not");
    }

    @Test
    void testSendMailMailReceived() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");
        outlook.addNewAccount("stoyo", "stoyo@fmi.bg");

        String metadata =
            "subject: Hello, MJT!" + System.lineSeparator() +
                "recipients: stoyo@fmi.bg" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        outlook.sendMail("test", metadata, "lorem ipsum");

        Account account = new Account("stoyo@fmi.bg", "stoyo");
        HashSet<String> recipients = new HashSet<>();
        recipients.add("stoyo@fmi.bg");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2022-12-08 14:14", formatter);

        Collection<Mail> expected = new ArrayList<>();
        expected.add(new Mail(account, recipients, "Hello, MJT!", "lorem ipsum", dateTime));

        assertArrayEquals(expected.toArray(),
            outlook.getMailsFromFolder("stoyo", "inbox").toArray(),
            "Receiver should receive mail but did not");
    }

    @Test
    void testAddRuleUpdateCurrentFiles() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("test", "testy@gmail.com");
        outlook.addNewAccount("stoyo", "stoyo@fmi.bg");

        outlook.createFolder("test", "inbox/important");
        outlook.createFolder("test", "inbox/stuff");

        String ruleDefinitionImportant = "subject-includes: MJT";
        String ruleDefinitionStuff = "recipients-includes: misho@gmail.com";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2022-12-08 14:14", formatter);
        Account account = new Account("stoyo@fmi.bg", "stoyo");

        HashSet<String> recipients1 = new HashSet<>();
        recipients1.add("misho@gmail.com");
        String metadata1 =
            "sender: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients: misho@gmail.com" + System.lineSeparator() +
                "subject: Yo!" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        Mail mail1 = new Mail(account, recipients1, "Yo!", "lorem ipsum", dateTime);

        HashSet<String> recipients2 = new HashSet<>();
        recipients2.add("gosho@gmail.com");
        recipients2.add("misho@fmi.bg");
        String metadata2 =
            "sender: stoyo@fmi.bg" + System.lineSeparator() +
                "recipients: misho@fmi.bg, gosho@gmail.com" + System.lineSeparator() +
                "subject: MJT!" + System.lineSeparator() +
                "received: 2022-12-08 14:14";

        Mail mail2 = new Mail(account, recipients2, "MJT!", "lorem ipsum", dateTime);

        outlook.receiveMail("test", metadata1, "lorem ipsum");
        outlook.receiveMail("test", metadata2, "lorem ipsum");

        outlook.addRule("test", "inbox/important", ruleDefinitionImportant, 1);

        assertArrayEquals(new Mail[] {mail2},
            outlook.getMailsFromFolder("test", "inbox/important").toArray(),
            "Mails should be updated for each new rule but are not");
        assertArrayEquals(new Mail[] {mail1},
            outlook.getMailsFromFolder("test", "inbox").toArray(),
            "Mails should be updated for each new rule but are not");

        outlook.addRule("test", "inbox/stuff", ruleDefinitionStuff, 7);

        assertArrayEquals(new Mail[] {mail2},
            outlook.getMailsFromFolder("test", "inbox/important").toArray(),
            "Mails should be updated for each new rule but are not");
        assertArrayEquals(new Mail[] {mail1},
            outlook.getMailsFromFolder("test", "inbox/stuff").toArray(),
            "Mails should be updated for each new rule but are not");
    }
}