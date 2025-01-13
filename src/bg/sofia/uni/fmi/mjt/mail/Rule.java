package bg.sofia.uni.fmi.mjt.mail;

import java.util.HashSet;
import java.util.Set;

public record Rule(String accountName, String folderPath, int priority,
                   Set<String> subjectKeywords, Set<String> subjectOrBodyKeywords,
                   Set<String> recipients, Set<String> sender) {
}