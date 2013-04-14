package net.buddat.bungeechat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.Colors;

/**
 * Recolourises MC-formatted messages for IRC
 */
public class MessageColouriser {
    private static final Map<String, String> REPLACEMENT_MAP;
    private static final String COLOUR_CHAR = "\u00A7";
    
    static {
        Map<String, String> charReplacementMap = new HashMap<String, String>();
        charReplacementMap.put("0", Colors.WHITE);
        charReplacementMap.put("1", Colors.DARK_BLUE);
        charReplacementMap.put("2", Colors.DARK_GREEN);
        charReplacementMap.put("3", Colors.TEAL);
        charReplacementMap.put("4", Colors.BROWN);
        charReplacementMap.put("5", Colors.PURPLE);
        charReplacementMap.put("6", Colors.OLIVE);
        charReplacementMap.put("7", Colors.LIGHT_GRAY);
        charReplacementMap.put("8", Colors.DARK_GRAY);
        charReplacementMap.put("9", Colors.BLUE);
        charReplacementMap.put("a", Colors.GREEN);
        charReplacementMap.put("b", Colors.CYAN);
        charReplacementMap.put("c", Colors.RED);
        charReplacementMap.put("d", Colors.MAGENTA);
        charReplacementMap.put("e", Colors.YELLOW);
        charReplacementMap.put("f", Colors.WHITE);
        charReplacementMap.put("k", "");
        charReplacementMap.put("l", Colors.BOLD);
        charReplacementMap.put("m", "");
        charReplacementMap.put("n", Colors.UNDERLINE);
        charReplacementMap.put("o", "");
        charReplacementMap.put("p", Colors.NORMAL);
        REPLACEMENT_MAP = Collections.unmodifiableMap(charReplacementMap);
    }
    final Pattern mcColours = Pattern.compile(COLOUR_CHAR + "([0-9A-FK-Pa-fk-p])");

    public MessageColouriser() {}
    
    public String mcToIrc(String message) {
        Matcher matcher = mcColours.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group(1);
            String replacement = REPLACEMENT_MAP.get(group.toLowerCase());
            matcher.appendReplacement(sb, replacement == null ? "" : replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
