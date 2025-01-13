package bg.sofia.uni.fmi.mjt.mail.comparators;

import bg.sofia.uni.fmi.mjt.mail.Rule;

import java.util.Comparator;

public class RuleComparator implements Comparator<Rule> {

    @Override
    public int compare(Rule o1, Rule o2) {
        return Integer.compare(o1.priority(), o2.priority());
    }
}
