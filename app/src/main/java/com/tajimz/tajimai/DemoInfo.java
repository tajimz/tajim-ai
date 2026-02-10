package com.tajimz.tajimai;

public class DemoInfo {

    // 1. Basic Personal Info
    private static final String BASIC_INFO = "{\n" +
            "  \"full_name\": \"John Doe\",\n" +
            "  \"gender\": \"male\",\n" +
            "  \"date_of_birth\": \"2000-01-01\",\n" +
            "  \"age\": 23,\n" +
            "  \"nationality\": \"DemoLand\",\n" +
            "  \"relationship_status\": \"single\",\n" +
            "  \"siblings\": \"1 brother, 1 sister\",\n" +
            "  \"blood_group\": \"A+\",\n" +
            "  \"language_spoken\": [\"English\", \"DemoLanguage\"]\n" +
            "}";

    // 2. Location Info
    private static final String LOCATION = "{\n" +
            "  \"current\": \"Demo City, DemoState\",\n" +
            "  \"previous\": \"Old DemoTown, DemoState\",\n" +
            "  \"timezone\": \"+0 GMT\",\n" +
            "  \"country\": \"DemoCountry\"\n" +
            "}";

    // 3. Education & Profession
    private static final String EDUCATION_PROFESSION = "{\n" +
            "  \"education\": \"Demo High School\",\n" +
            "  \"school\": \"Demo High School\",\n" +
            "  \"profession\": \"Student / Developer\",\n" +
            "  \"skills\": [\"Java\", \"Python\", \"C++\"],\n" +
            "  \"certifications\": [\"Demo Certification 1\", \"Demo Certification 2\"],\n" +
            "  \"projects_completed\": [\"Demo App 1\", \"Demo Website\"]\n" +
            "}";

    // 4. Interests & Hobbies
    private static final String INTERESTS_LIFESTYLE = "{\n" +
            "  \"hobbies\": [\"Reading\", \"Gaming\", \"Music\"],\n" +
            "  \"interests\": [\"Tech\", \"AI\", \"Programming\"],\n" +
            "  \"favorite_music\": [\"Pop\", \"Rock\"],\n" +
            "  \"favorite_movies\": [\"Demo Movie 1\", \"Demo Movie 2\"],\n" +
            "  \"favorite_books\": [\"Demo Book 1\", \"Demo Book 2\"],\n" +
            "  \"favorite_foods\": [\"Pizza\", \"Burger\"],\n" +
            "  \"sports_played\": [\"Football\", \"Basketball\"],\n" +
            "  \"travel_interest\": \"high\"\n" +
            "}";

    // 5. Communication & Personality
    private static final String COMMUNICATION_PERSONALITY = "{\n" +
            "  \"communication_style\": \"friendly and clear\",\n" +
            "  \"personality_traits\": [\"curious\", \"friendly\", \"organized\"],\n" +
            "  \"social_behavior\": \"open and sociable\"\n" +
            "}";

    // 6. Contact & Social Media
    private static final String CONTACT_SOCIALS = "{\n" +
            "  \"email\": \"demo@email.com\",\n" +
            "  \"phone\": \"+000123456789\",\n" +
            "  \"social_links\": {\n" +
            "    \"facebook\": \"facebook.com/demo\",\n" +
            "    \"instagram\": \"instagram.com/demo\",\n" +
            "    \"twitter\": \"twitter.com/demo\",\n" +
            "    \"linkedin\": \"linkedin.com/in/demo\"\n" +
            "  },\n" +
            "  \"websites\": [\"https://demo.com\"]\n" +
            "}";

    // 7. Health & Fitness
    private static final String HEALTH_FITNESS = "{\n" +
            "  \"height_cm\": 175,\n" +
            "  \"weight_kg\": 70,\n" +
            "  \"blood_group\": \"A+\",\n" +
            "  \"sleep_schedule\": \"11 PM\",\n" +
            "  \"allergies\": [\"None\"],\n" +
            "  \"fitness_activities\": [\"Running\", \"Gym\"]\n" +
            "}";

    // 8. Technology & Gadgets
    private static final String TECH_GADGETS = "{\n" +
            "  \"primary_os\": \"Linux (DemoOS)\",\n" +
            "  \"primary_device\": \"Demo PC (Demo CPU, 16GB RAM, 1TB SSD)\",\n" +
            "  \"secondary_devices\": [\"Demo Phone\"],\n" +
            "  \"favorite_software\": [\"Demo IDE\", \"Demo Editor\"],\n" +
            "  \"gadgets_owned\": [\"Laptop\", \"Smartphone\"]\n" +
            "}";

    // 9. Goals & Aspirations
    private static final String GOALS_ASPIRATIONS = "{\n" +
            "  \"short_term_goals\": [\"Learn Demo Tech\", \"Build Demo App\"],\n" +
            "  \"long_term_goals\": [\"Become Demo Developer\", \"Create Demo Startup\"],\n" +
            "  \"personal_aspirations\": [\"Travel to DemoLand\", \"Master Demo Skills\"]\n" +
            "}";

    // Method to get JSON based on category key
    public static String getInfo(String key) {
        switch (key) {
            case "basic_info":
                return BASIC_INFO;
            case "location":
                return LOCATION;
            case "education_profession":
                return EDUCATION_PROFESSION;
            case "interests_lifestyle":
                return INTERESTS_LIFESTYLE;
            case "communication_personality":
                return COMMUNICATION_PERSONALITY;
            case "contact_socials":
                return CONTACT_SOCIALS;
            case "health_fitness":
                return HEALTH_FITNESS;
            case "tech_gadgets":
                return TECH_GADGETS;
            case "goals_aspirations":
                return GOALS_ASPIRATIONS;

            default:
                return "{}"; // empty JSON for invalid key
        }
    }
}
