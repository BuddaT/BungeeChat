package net.buddat.bungeechat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.Colors;

/**
 * Translates colours in MC-formatted messages to their IRC equivalents and vice versa.
 */
public class MessageColourTranslater {
    private static final Map<String, String> DEFAULT_MC_IRC_REPLACEMENT_MAP;
    private static final Map<String, String> DEFAULT_IRC_MC_REPLACEMENT_MAP;
    private static final String MC_COLOUR_CHAR = "\u00A7";
    private static final String EMPTY_STRING = "";
    
    static {
        Map<String, String> mcIrcReplacementMap = new HashMap<String, String>();
        Map<String, String> ircMcReplacementMap = new HashMap<String, String>();
        String[] tuples = new String[] {
            "0", Colors.WHITE,
            "1", Colors.DARK_BLUE,
            "2", Colors.DARK_GREEN,
            "3", Colors.TEAL,
            "4", Colors.BROWN,
            "5", Colors.PURPLE,
            "6", Colors.OLIVE,
            "7", Colors.LIGHT_GRAY,
            "8", Colors.DARK_GRAY,
            "9", Colors.BLUE,
            "a", Colors.GREEN,
            "b", Colors.CYAN,
            "c", Colors.RED,
            "d", Colors.MAGENTA,
            "e", Colors.YELLOW,
            "f", Colors.NORMAL,
            "k", EMPTY_STRING,
            "l", Colors.BOLD,
            "m", EMPTY_STRING,
            "n", Colors.UNDERLINE,
            "o", EMPTY_STRING,
            "p", Colors.NORMAL
        };
        for (int i = 0; i < tuples.length; i+= 2) {
            mcIrcReplacementMap.put(tuples[i], tuples[i + 1]);
            if (tuples[i + 1].length() > 0) {
                ircMcReplacementMap.put(tuples[i + 1], MC_COLOUR_CHAR + tuples[i]);
            }
        }
        DEFAULT_MC_IRC_REPLACEMENT_MAP = Collections.unmodifiableMap(mcIrcReplacementMap);
        DEFAULT_IRC_MC_REPLACEMENT_MAP = Collections.unmodifiableMap(ircMcReplacementMap);
    }
    private static final Pattern mcColours = Pattern.compile(MC_COLOUR_CHAR + "([0-9A-FK-Pa-fk-p])");
    private static final Pattern ircColours;
    // TODO: This is pretty bad
    static {
        StringBuilder regex = new StringBuilder();
        regex.append("(\\x03");
        Iterator<String> iter = DEFAULT_IRC_MC_REPLACEMENT_MAP.keySet().iterator();
        regex.append("(");
        boolean first = true;
        while(iter.hasNext()) {
            String colour = iter.next();
            if (colour.length() >= 2) {
                if (first) {
                    first = false;
                } else {
                    regex.append("|");
                }
                regex.append(colour.substring(1));
            }
        }
        regex.append("))");
        ircColours = Pattern.compile(regex.toString());
        System.out.println("IRC pattern: " + regex.toString());
    }
    

    private final Map<String, String> ircMcReplacementMap;
    private final Map<String, String> mcIrcReplacementMap;

    /**
     * Creates a message colour translater with the default colour maps.
     */
    public MessageColourTranslater() {
        this(DEFAULT_MC_IRC_REPLACEMENT_MAP, DEFAULT_IRC_MC_REPLACEMENT_MAP);
    }

    /**
     * Creates a message colour translater with the specified colour maps.
     * 
     * @param mcIrcReplacementMap
     * @param ircMcReplacementMap
     */
    public MessageColourTranslater(Map<String, String> mcIrcReplacementMap, Map<String, String> ircMcReplacementMap) {
        this.mcIrcReplacementMap = mcIrcReplacementMap;
        this.ircMcReplacementMap = ircMcReplacementMap;
    }
    
    /**
     * Translates a minecraft colour-encoded string to one encoded with the IRC
     * equivalents. Colour equivalents are based on the configured colours, with
     * defaults used for the closest approximation.
     * 
     * @param message
     *            Message to translate.
     * @return Translated message string. If the message is null, an empty
     *         string is returned.
     */
    public String mcToIrc(String message) {
        return translate(message, mcColours, mcIrcReplacementMap);
    }
    
    /**
     * Translates an IRC colour-encoded string to one encoded with the minecraft
     * equivalents. Colour equivalents are based on the configured colours, with
     * defaults used for the closest approximation.
     * 
     * @param message
     *            Message to translate.
     * @return Translated message string. If the message is null, an empty
     *         string is returned.
     */
    public String ircToMc(String message) {
        return translate(message, ircColours, ircMcReplacementMap);
    }
    
    public String translate(String message, Pattern colourPattern, Map<String, String> replacementMap) {
        if (message == null) {
            return EMPTY_STRING;
        }
        Matcher matcher = colourPattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        // TODO: replace this with callback of some kind
        while (matcher.find()) {
            String group = matcher.group(1);
            String replacement = replacementMap.get(group.toLowerCase());
            matcher.appendReplacement(sb, replacement == null ? "" : replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
