package com.tajimz.tajimai;

public class DemoInfo {

    // 1. Basic Personal Info
    private static final String BASIC_INFO = "{\n" +
            "  \"full_name\": \"TR Tajim\",\n" +
            "  \"gender\": \"male\",\n" +
            "  \"date_of_birth\": \"2008-08-18\",\n" +
            "  \"age\": 17,\n" +
            "  \"nationality\": \"Bangladeshi\",\n" +
            "  \"relationship_status\": \"single\",\n" +
            "  \"siblings\": \"elder brother and younger sister\",\n" +
            "  \"blood_group\": \"O+\",\n" +
            "  \"language_spoken\": [\"English\", \"Bangla\"]\n" +
            "}";

    // 2. Location Info
    private static final String LOCATION = "{\n" +
            "  \"current\": \"Kamal Kasna, Rangpur\",\n" +
            "  \"previous\": \"Nabdiganj, Rangpur\",\n" +
            "  \"timezone\": \"+6 GMT\",\n" +
            "  \"country\": \"Bangladesh\"\n" +
            "}";

    // 3. Education & Profession
    private static final String EDUCATION_PROFESSION = "{\n" +
            "  \"education\": \"High School (Class 10, A Section, Roll 17)\",\n" +
            "  \"school\": \"Rangpur Zilla School\",\n" +
            "  \"profession\": \"Student / Programmer\",\n" +
            "  \"skills\": [\"Java\", \"PHP\", \"Python\", \"C++\", \"SQL\", \"Android Development\"],\n" +
            "  \"certifications\": [\"Fiverr Projects\", \"Android App Development\"],\n" +
            "  \"projects_completed\": [\"Private Android Chat App\", \"Portfolio Website\"]\n" +
            "}";

    // 4. Interests & Hobbies
    private static final String INTERESTS_LIFESTYLE = "{\n" +
            "  \"hobbies\": [\"Coding\", \"Tech Research\", \"Gaming\"],\n" +
            "  \"interests\": [\"Android Apps\", \"Linux\", \"AI Development\"],\n" +
            "  \"favorite_music\": [\"English Pop\", \"Bangla Songs\"],\n" +
            "  \"favorite_movies\": [],\n" +
            "  \"favorite_books\": [],\n" +
            "  \"favorite_foods\": [\"Rice\", \"Chicken Curry\"],\n" +
            "  \"sports_played\": [\"Football\", \"Badminton\"],\n" +
            "  \"travel_interest\": \"moderate\"\n" +
            "}";

    // 5. Communication & Personality
    private static final String COMMUNICATION_PERSONALITY = "{\n" +
            "  \"communication_style\": \"casual and direct, short responses\",\n" +
            "  \"personality_traits\": [\"analytical\", \"curious\", \"focused\", \"introverted\"],\n" +
            "  \"social_behavior\": \"friendly with close circle, reserved with strangers\"\n" +
            "}";

    // 6. Contact & Social Media
    private static final String CONTACT_SOCIALS = "{\n" +
            "  \"email\": \"example@email.com\",\n" +
            "  \"phone\": \"+880123456789\",\n" +
            "  \"social_links\": {\n" +
            "    \"facebook\": \"facebook.com/trtajim.xyz\",\n" +
            "    \"instagram\": \"instagram.com/trtajim\",\n" +
            "    \"twitter\": \"twitter.com/trtajim\",\n" +
            "    \"linkedin\": \"linkedin.com/in/trtajim\"\n" +
            "  },\n" +
            "  \"websites\": [\"https://trtajim.xyz\"]\n" +
            "}";

    // 7. Health & Fitness
    private static final String HEALTH_FITNESS = "{\n" +
            "  \"height_cm\": 170,\n" +
            "  \"weight_kg\": 60,\n" +
            "  \"blood_group\": \"O+\",\n" +
            "  \"sleep_schedule\": \"11 PM\",\n" +
            "  \"allergies\": [],\n" +
            "  \"fitness_activities\": [\"Walking\", \"Gym occasional\"]\n" +
            "}";

    // 8. Technology & Gadgets
    private static final String TECH_GADGETS = "{\n" +
            "  \"primary_os\": \"Linux (Ubuntu)\",\n" +
            "  \"primary_device\": \"PC (Ryzen 7 7700, 16GB DDR5, 1TB Gen4 SSD)\",\n" +
            "  \"secondary_devices\": [\"Android Phone\"],\n" +
            "  \"favorite_software\": [\"Android Studio\", \"VS Code\", \"PhpStorm\"],\n" +
            "  \"gadgets_owned\": [\"Laptop\", \"Smartphone\", \"Smartwatch\"]\n" +
            "}";

    // 9. Goals & Aspirations
    private static final String GOALS_ASPIRATIONS = "{\n" +
            "  \"short_term_goals\": [\"Build personal Android apps\", \"Learn AI integration\"],\n" +
            "  \"long_term_goals\": [\"Become a full-stack developer\", \"Create tech startup\"],\n" +
            "  \"personal_aspirations\": [\"Master coding\", \"Travel abroad\"]\n" +
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
