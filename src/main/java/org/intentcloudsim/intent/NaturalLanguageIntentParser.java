package org.intentcloudsim.intent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PATENT IDEA 16: Natural Language to Cloud Intent Parser
 *
 * Converts human-readable text like "I want fast and cheap servers"
 * into an Intent vector that the cloud system can understand.
 *
 * NOVELTY: No existing cloud system converts natural language
 * directly into virtualization parameters.
 *
 * ENHANCEMENTS:
 * - Advanced keyword matching with phrase support
 * - Negation detection ("don't care about cost")
 * - Intensity modifiers ("very fast", "extremely cheap")
 * - Context-aware prioritization
 * - Confidence scoring
 */
public class NaturalLanguageIntentParser {

    private static final SemanticIntentMapper SEMANTIC_MAPPER =
        new SemanticIntentMapper();

    private static final double KEYWORD_WEIGHT = 0.45;
    private static final double SEMANTIC_WEIGHT = 0.55;

    // Keywords that indicate cost priority
    private static final Map<String, Double> COST_KEYWORDS = new HashMap<>();

    // Keywords that indicate latency/speed priority
    private static final Map<String, Double> LATENCY_KEYWORDS = new HashMap<>();

    // Keywords that indicate security priority
    private static final Map<String, Double> SECURITY_KEYWORDS = new HashMap<>();

    // Keywords that indicate carbon/green priority
    private static final Map<String, Double> CARBON_KEYWORDS = new HashMap<>();

    // Intensity modifiers for keyword matching
    private static final Map<String, Double> INTENSITY_MODIFIERS = new HashMap<>();

    // Negation patterns
    private static final List<Pattern> NEGATION_PATTERNS = new ArrayList<>();

    // Initialize all keywords and their weights
    static {
        // Cost-related keywords
        COST_KEYWORDS.put("cheap", 0.9);
        COST_KEYWORDS.put("budget", 0.85);
        COST_KEYWORDS.put("affordable", 0.8);
        COST_KEYWORDS.put("economical", 0.8);
        COST_KEYWORDS.put("low cost", 0.9);
        COST_KEYWORDS.put("save money", 0.85);
        COST_KEYWORDS.put("inexpensive", 0.8);
        COST_KEYWORDS.put("cost-effective", 0.75);
        COST_KEYWORDS.put("minimize cost", 0.9);
        COST_KEYWORDS.put("free tier", 0.95);
        COST_KEYWORDS.put("low price", 0.85);
        COST_KEYWORDS.put("budget friendly", 0.9);
        COST_KEYWORDS.put("money", 0.7);
        COST_KEYWORDS.put("expense", 0.75);
        COST_KEYWORDS.put("billing", 0.7);

        // Speed/latency-related keywords
        LATENCY_KEYWORDS.put("fast", 0.9);
        LATENCY_KEYWORDS.put("quick", 0.85);
        LATENCY_KEYWORDS.put("rapid", 0.85);
        LATENCY_KEYWORDS.put("low latency", 0.95);
        LATENCY_KEYWORDS.put("real-time", 0.95);
        LATENCY_KEYWORDS.put("responsive", 0.8);
        LATENCY_KEYWORDS.put("high performance", 0.9);
        LATENCY_KEYWORDS.put("speed", 0.85);
        LATENCY_KEYWORDS.put("instant", 0.9);
        LATENCY_KEYWORDS.put("gaming", 0.85);
        LATENCY_KEYWORDS.put("streaming", 0.8);
        LATENCY_KEYWORDS.put("performance", 0.85);
        LATENCY_KEYWORDS.put("throughput", 0.8);
        LATENCY_KEYWORDS.put("efficient", 0.75);
        LATENCY_KEYWORDS.put("optimized", 0.8);

        // Security-related keywords
        SECURITY_KEYWORDS.put("secure", 0.9);
        SECURITY_KEYWORDS.put("encrypted", 0.85);
        SECURITY_KEYWORDS.put("private", 0.8);
        SECURITY_KEYWORDS.put("confidential", 0.85);
        SECURITY_KEYWORDS.put("compliant", 0.8);
        SECURITY_KEYWORDS.put("hipaa", 0.95);
        SECURITY_KEYWORDS.put("gdpr", 0.9);
        SECURITY_KEYWORDS.put("isolated", 0.85);
        SECURITY_KEYWORDS.put("protected", 0.8);
        SECURITY_KEYWORDS.put("banking", 0.9);
        SECURITY_KEYWORDS.put("healthcare", 0.9);
        SECURITY_KEYWORDS.put("safety", 0.85);
        SECURITY_KEYWORDS.put("privacy", 0.85);
        SECURITY_KEYWORDS.put("integrity", 0.8);
        SECURITY_KEYWORDS.put("authentication", 0.8);

        // Carbon/sustainability keywords
        CARBON_KEYWORDS.put("green", 0.9);
        CARBON_KEYWORDS.put("sustainable", 0.85);
        CARBON_KEYWORDS.put("eco", 0.85);
        CARBON_KEYWORDS.put("carbon neutral", 0.95);
        CARBON_KEYWORDS.put("renewable", 0.9);
        CARBON_KEYWORDS.put("environment", 0.8);
        CARBON_KEYWORDS.put("low carbon", 0.9);
        CARBON_KEYWORDS.put("energy efficient", 0.85);
        CARBON_KEYWORDS.put("efficient", 0.7);
        CARBON_KEYWORDS.put("sustainable", 0.85);

        // Intensity modifiers
        INTENSITY_MODIFIERS.put("very", 1.1);
        INTENSITY_MODIFIERS.put("extremely", 1.2);
        INTENSITY_MODIFIERS.put("highly", 1.1);
        INTENSITY_MODIFIERS.put("super", 1.15);
        INTENSITY_MODIFIERS.put("ultra", 1.2);
        INTENSITY_MODIFIERS.put("absolutely", 1.15);
        INTENSITY_MODIFIERS.put("critically", 1.2);
        INTENSITY_MODIFIERS.put("really", 1.05);

        // Negation patterns
        NEGATION_PATTERNS.add(Pattern.compile("don't.*care.*about|not.*important", Pattern.CASE_INSENSITIVE));
        NEGATION_PATTERNS.add(Pattern.compile("ignore.*|skip.*|no need.*", Pattern.CASE_INSENSITIVE));
        NEGATION_PATTERNS.add(Pattern.compile("not.*priority|low.*priority", Pattern.CASE_INSENSITIVE));
    }

    /**
     * Parse a natural language string into an Intent object.
     *
     * @param input The user's natural language request
     * @return An Intent object with appropriate priorities
     */
    public static Intent parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("[IntentParser] Empty input, returning default intent");
            return new Intent(); // default balanced intent
        }

        String lowerInput = input.toLowerCase().trim();

    double keywordCostScore = calculateScore(lowerInput, COST_KEYWORDS);
    double keywordLatencyScore = calculateScore(lowerInput, LATENCY_KEYWORDS);
    double keywordSecurityScore = calculateScore(lowerInput, SECURITY_KEYWORDS);
    double keywordCarbonScore = calculateScore(lowerInput, CARBON_KEYWORDS);

    Map<String, Double> semanticScores = SEMANTIC_MAPPER.extractScores(lowerInput);

    double costScore = combine(keywordCostScore, semanticScores.getOrDefault("cost", 0.0));
    double latencyScore = combine(keywordLatencyScore, semanticScores.getOrDefault("latency", 0.0));
    double securityScore = combine(keywordSecurityScore, semanticScores.getOrDefault("security", 0.0));
    double carbonScore = combine(keywordCarbonScore, semanticScores.getOrDefault("carbon", 0.0));

        // Apply negation detection
        costScore = applyNegation(lowerInput, costScore);
        latencyScore = applyNegation(lowerInput, latencyScore);
        securityScore = applyNegation(lowerInput, securityScore);
        carbonScore = applyNegation(lowerInput, carbonScore);

        // Normalize scores
        normalizeScores(costScore, latencyScore, securityScore, carbonScore);

        // If no keywords matched, use moderate defaults
        if (costScore == 0 && latencyScore == 0 &&
            securityScore == 0 && carbonScore == 0) {
            costScore = 0.5;
            latencyScore = 0.5;
            securityScore = 0.3;
            carbonScore = 0.2;
        }

        Intent intent = new Intent(costScore, latencyScore,
                                    securityScore, carbonScore);

        System.out.println("[IntentParser] Input: \"" + input + "\"");
        System.out.printf("[IntentParser] Semantic: cost=%.2f, latency=%.2f, security=%.2f, carbon=%.2f%n",
            semanticScores.getOrDefault("cost", 0.0),
            semanticScores.getOrDefault("latency", 0.0),
            semanticScores.getOrDefault("security", 0.0),
            semanticScores.getOrDefault("carbon", 0.0));
        System.out.println("[IntentParser] Parsed: " + intent);

        return intent;
    }

    /**
     * Calculate how strongly the input matches a keyword category.
     */
    private static double calculateScore(String input,
                                          Map<String, Double> keywords) {
        double maxScore = 0.0;
        int matchCount = 0;
        double weightedSum = 0.0;

        for (Map.Entry<String, Double> entry : keywords.entrySet()) {
            String keyword = entry.getKey();
            Double weight = entry.getValue();

            if (input.contains(keyword)) {
                // Apply intensity modifiers
                double modifiedWeight = applyIntensityModifier(input, keyword, weight);
                weightedSum += modifiedWeight;
                maxScore = Math.max(maxScore, modifiedWeight);
                matchCount++;
            }
        }

        // Average if multiple keywords match, but keep it weighted
        if (matchCount > 0) {
            maxScore = Math.min(1.0, weightedSum / Math.max(1, matchCount));

            // Bonus for multiple keyword matches (shows strong intent)
            if (matchCount > 1) {
                maxScore = Math.min(1.0, maxScore + 0.05 * (matchCount - 1));
            }
        }

        return maxScore;
    }

    private static double combine(double keywordScore, double semanticScore) {
        double weighted = (KEYWORD_WEIGHT * keywordScore) +
                          (SEMANTIC_WEIGHT * semanticScore);

        double strongSignal = Math.max(keywordScore, semanticScore);
        if (strongSignal >= 0.75) {
            weighted = Math.max(weighted, strongSignal * 0.90);
        }

        return Math.max(0.0, Math.min(1.0, weighted));
    }

    /**
     * Apply intensity modifiers like "very", "extremely" to keyword weights.
     */
    private static double applyIntensityModifier(String input, String keyword, double weight) {
        // Find all modifiers near the keyword
        double modifier = 1.0;
        String[] words = input.split("\\s+");

        for (int i = 0; i < words.length - 1; i++) {
            if (words[i + 1].contains(keyword) || keyword.contains(words[i + 1])) {
                Double mod = INTENSITY_MODIFIERS.get(words[i]);
                if (mod != null) {
                    modifier = Math.max(modifier, mod);
                }
            }
        }

        return Math.min(1.0, weight * modifier);
    }

    /**
     * Apply negation detection to reduce scores if negated.
     */
    private static double applyNegation(String input, double score) {
        for (Pattern pattern : NEGATION_PATTERNS) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return 0.0; // Strong negation reduces to 0
            }
        }
        return score;
    }

    /**
     * Normalize scores to sum to approximately 1.0 if multiple priorities are set.
     */
    private static double[] normalizeScores(double cost, double latency,
                                            double security, double carbon) {
        double sum = cost + latency + security + carbon;
        if (sum > 1.0) {
            return new double[]{cost / sum, latency / sum, security / sum, carbon / sum};
        }
        return new double[]{cost, latency, security, carbon};
    }

    /**
     * Get a human-readable explanation of the parsing.
     */
    public static String explain(String input) {
        Intent intent = parse(input);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Intent Parsing Explanation ===\n");
        sb.append("Input: \"").append(input).append("\"\n");
        sb.append("Cost Priority: ").append(
            String.format("%.0f%%", intent.getCostPriority() * 100)).append("\n");
        sb.append("Latency Priority: ").append(
            String.format("%.0f%%", intent.getLatencyPriority() * 100)).append("\n");
        sb.append("Security Priority: ").append(
            String.format("%.0f%%", intent.getSecurityPriority() * 100)).append("\n");
        sb.append("Carbon Priority: ").append(
            String.format("%.0f%%", intent.getCarbonPriority() * 100)).append("\n");
        return sb.toString();
    }

    /**
     * Parse with confidence score feedback.
     */
    public static ParseResult parseWithConfidence(String input) {
        Intent intent = parse(input);
        double confidence = calculateConfidence(input);
        return new ParseResult(intent, confidence, extractDominantPriority(intent));
    }

    /**
     * Calculate confidence of the parsing based on keyword coverage.
     */
    private static double calculateConfidence(String input) {
        int totalMatches = 0;
        String lowerInput = input.toLowerCase();

        for (Map<String, Double> keywordMap : Arrays.asList(
                COST_KEYWORDS, LATENCY_KEYWORDS, SECURITY_KEYWORDS, CARBON_KEYWORDS)) {
            for (String keyword : keywordMap.keySet()) {
                if (lowerInput.contains(keyword)) {
                    totalMatches++;
                }
            }
        }

        // Confidence based on number of matched keywords
        // More keywords = higher confidence that we understood the intent
        return Math.min(1.0, 0.3 + (totalMatches * 0.15));
    }

    /**
     * Determine which priority is dominant.
     */
    private static String extractDominantPriority(Intent intent) {
        double[] priorities = {
            intent.getCostPriority(),
            intent.getLatencyPriority(),
            intent.getSecurityPriority(),
            intent.getCarbonPriority()
        };

        String[] names = {"Cost", "Latency", "Security", "Carbon"};
        int maxIdx = 0;
        double maxVal = priorities[0];

        for (int i = 1; i < priorities.length; i++) {
            if (priorities[i] > maxVal) {
                maxVal = priorities[i];
                maxIdx = i;
            }
        }

        return names[maxIdx];
    }

    /**
     * Result class for parsing with confidence.
     */
    public static class ParseResult {
        public final Intent intent;
        public final double confidence;
        public final String dominantPriority;

        public ParseResult(Intent intent, double confidence, String dominantPriority) {
            this.intent = intent;
            this.confidence = confidence;
            this.dominantPriority = dominantPriority;
        }

        @Override
        public String toString() {
            return String.format("ParseResult[intent=%s, confidence=%.0f%%, dominant=%s]",
                intent, confidence * 100, dominantPriority);
        }
    }
}

