package com.smartresume.service;

import com.smartresume.model.Resume;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SmartATS — Enhanced MatchingService v4
 * ────────────────────────────────────────
 * ✅ 40 job roles (was 20)
 * ✅ Expanded skill weight dictionary (200+ skills)
 * ✅ Experience-level detection
 * ✅ Cosine-boost from job description
 * ✅ Better suggestions and role recommendations
 */
// Upgrade this method to generate professional ATS analysis.
// Add the following features:
// 1. Calculate skillsMatch, keywordMatch, and experienceMatch separately.
// 2. Generate a "whyScore" explanation explaining why candidate got this score.
// 3. Generate a "profileSummary" describing candidate strengths and weaknesses.
// 4. Generate a "hiringDecision" such as:
//    - Strong Candidate (>80)
//    - Moderate Fit (60-80)
//    - Needs Improvement (<60)
// 5. Generate structured improvementPlan:
//    - skill -> why needed -> how to improve
// 6. Ensure at least 5 improvement suggestions.
// 7. Keep logic simple using keyword matching (no AI API).
@Service
public class MatchingService {

        // ── Skill weight map ──────────────────────────────────────────────
        private static final Map<String, Integer> SKILL_WEIGHTS = new LinkedHashMap<>();
        static {
                // Languages
                SKILL_WEIGHTS.put("java", 10);
                SKILL_WEIGHTS.put("python", 10);
                SKILL_WEIGHTS.put("javascript", 8);
                SKILL_WEIGHTS.put("typescript", 8);
                SKILL_WEIGHTS.put("c++", 8);
                SKILL_WEIGHTS.put("c#", 8);
                SKILL_WEIGHTS.put("kotlin", 8);
                SKILL_WEIGHTS.put("scala", 8);
                SKILL_WEIGHTS.put("go", 7);
                SKILL_WEIGHTS.put("rust", 7);
                SKILL_WEIGHTS.put("swift", 8);
                SKILL_WEIGHTS.put("dart", 7);
                SKILL_WEIGHTS.put("r", 6);
                SKILL_WEIGHTS.put("bash", 6);
                SKILL_WEIGHTS.put("shell scripting", 7);
                SKILL_WEIGHTS.put("php", 7);
                SKILL_WEIGHTS.put("ruby", 7);
                SKILL_WEIGHTS.put("perl", 5);
                SKILL_WEIGHTS.put("matlab", 6);
                SKILL_WEIGHTS.put("julia", 6);
                SKILL_WEIGHTS.put("embedded c", 12);
                SKILL_WEIGHTS.put("solidity", 12);

                // Frameworks & Libraries
                SKILL_WEIGHTS.put("spring boot", 20);
                SKILL_WEIGHTS.put("spring", 12);
                SKILL_WEIGHTS.put("react", 15);
                SKILL_WEIGHTS.put("angular", 15);
                SKILL_WEIGHTS.put("vue", 12);
                SKILL_WEIGHTS.put("node.js", 12);
                SKILL_WEIGHTS.put("django", 12);
                SKILL_WEIGHTS.put("flask", 10);
                SKILL_WEIGHTS.put("fastapi", 10);
                SKILL_WEIGHTS.put("hibernate", 12);
                SKILL_WEIGHTS.put("express.js", 10);
                SKILL_WEIGHTS.put("next.js", 12);
                SKILL_WEIGHTS.put("nuxt.js", 10);
                SKILL_WEIGHTS.put("svelte", 10);
                SKILL_WEIGHTS.put("flutter", 12);
                SKILL_WEIGHTS.put("react native", 12);
                SKILL_WEIGHTS.put("html", 6);
                SKILL_WEIGHTS.put("css", 6);
                SKILL_WEIGHTS.put("rest api", 12);
                SKILL_WEIGHTS.put("graphql", 12);
                SKILL_WEIGHTS.put("microservices", 15);
                SKILL_WEIGHTS.put("responsive design", 8);
                SKILL_WEIGHTS.put("tailwind", 7);
                SKILL_WEIGHTS.put("bootstrap", 6);
                SKILL_WEIGHTS.put("sass", 6);
                SKILL_WEIGHTS.put("webpack", 7);
                SKILL_WEIGHTS.put("vite", 7);
                SKILL_WEIGHTS.put("storybook", 7);
                SKILL_WEIGHTS.put(".net", 12);
                SKILL_WEIGHTS.put("asp.net", 12);
                SKILL_WEIGHTS.put("laravel", 10);
                SKILL_WEIGHTS.put("rails", 10);
                SKILL_WEIGHTS.put("spring security", 10);
                SKILL_WEIGHTS.put("oauth2", 10);
                SKILL_WEIGHTS.put("jwt", 8);
                SKILL_WEIGHTS.put("grpc", 10);
                SKILL_WEIGHTS.put("apache kafka", 12);
                SKILL_WEIGHTS.put("rabbitmq", 10);
                SKILL_WEIGHTS.put("celery", 8);
                SKILL_WEIGHTS.put("kafka", 12);
                SKILL_WEIGHTS.put("jetpack compose", 10);
                SKILL_WEIGHTS.put("swiftui", 10);

                // Design
                SKILL_WEIGHTS.put("figma", 10);
                SKILL_WEIGHTS.put("adobe xd", 10);
                SKILL_WEIGHTS.put("sketch", 10);
                SKILL_WEIGHTS.put("invision", 8);
                SKILL_WEIGHTS.put("zeplin", 8);
                SKILL_WEIGHTS.put("adobe photoshop", 8);
                SKILL_WEIGHTS.put("adobe illustrator", 8);

                // Databases
                SKILL_WEIGHTS.put("mysql", 10);
                SKILL_WEIGHTS.put("sql", 8);
                SKILL_WEIGHTS.put("postgresql", 10);
                SKILL_WEIGHTS.put("mongodb", 10);
                SKILL_WEIGHTS.put("redis", 10);
                SKILL_WEIGHTS.put("oracle", 8);
                SKILL_WEIGHTS.put("nosql", 8);
                SKILL_WEIGHTS.put("cassandra", 10);
                SKILL_WEIGHTS.put("elasticsearch", 10);
                SKILL_WEIGHTS.put("neo4j", 10);
                SKILL_WEIGHTS.put("dynamodb", 10);
                SKILL_WEIGHTS.put("firebase", 8);
                SKILL_WEIGHTS.put("sqlite", 6);
                SKILL_WEIGHTS.put("mariadb", 8);
                SKILL_WEIGHTS.put("snowflake", 12);
                SKILL_WEIGHTS.put("bigquery", 12);
                SKILL_WEIGHTS.put("redshift", 10);
                SKILL_WEIGHTS.put("hbase", 10);
                SKILL_WEIGHTS.put("database administration", 12);
                SKILL_WEIGHTS.put("database design", 10);
                SKILL_WEIGHTS.put("query optimization", 10);
                SKILL_WEIGHTS.put("data warehouse", 12);

                // DevOps & Cloud
                SKILL_WEIGHTS.put("aws", 15);
                SKILL_WEIGHTS.put("azure", 15);
                SKILL_WEIGHTS.put("gcp", 15);
                SKILL_WEIGHTS.put("docker", 12);
                SKILL_WEIGHTS.put("kubernetes", 15);
                SKILL_WEIGHTS.put("jenkins", 10);
                SKILL_WEIGHTS.put("git", 8);
                SKILL_WEIGHTS.put("linux", 8);
                SKILL_WEIGHTS.put("ci/cd", 12);
                SKILL_WEIGHTS.put("terraform", 12);
                SKILL_WEIGHTS.put("ansible", 10);
                SKILL_WEIGHTS.put("prometheus", 8);
                SKILL_WEIGHTS.put("grafana", 8);
                SKILL_WEIGHTS.put("networking", 10);
                SKILL_WEIGHTS.put("virtualization", 8);
                SKILL_WEIGHTS.put("cloud architecture", 14);
                SKILL_WEIGHTS.put("serverless", 10);
                SKILL_WEIGHTS.put("helm", 10);
                SKILL_WEIGHTS.put("openshift", 10);
                SKILL_WEIGHTS.put("gitlab ci", 10);
                SKILL_WEIGHTS.put("github actions", 10);
                SKILL_WEIGHTS.put("datadog", 8);
                SKILL_WEIGHTS.put("splunk", 8);
                SKILL_WEIGHTS.put("elk stack", 10);
                SKILL_WEIGHTS.put("cloudformation", 10);
                SKILL_WEIGHTS.put("pulumi", 8);

                // AI / ML / Data
                SKILL_WEIGHTS.put("machine learning", 18);
                SKILL_WEIGHTS.put("deep learning", 18);
                SKILL_WEIGHTS.put("data analysis", 14);
                SKILL_WEIGHTS.put("tensorflow", 15);
                SKILL_WEIGHTS.put("pytorch", 15);
                SKILL_WEIGHTS.put("nlp", 15);
                SKILL_WEIGHTS.put("statistics", 12);
                SKILL_WEIGHTS.put("pandas", 10);
                SKILL_WEIGHTS.put("numpy", 10);
                SKILL_WEIGHTS.put("scikit-learn", 12);
                SKILL_WEIGHTS.put("data visualization", 10);
                SKILL_WEIGHTS.put("tableau", 10);
                SKILL_WEIGHTS.put("power bi", 10);
                SKILL_WEIGHTS.put("computer vision", 15);
                SKILL_WEIGHTS.put("feature engineering", 12);
                SKILL_WEIGHTS.put("model deployment", 12);
                SKILL_WEIGHTS.put("large language models", 18);
                SKILL_WEIGHTS.put("prompt engineering", 12);
                SKILL_WEIGHTS.put("rag", 14);
                SKILL_WEIGHTS.put("langchain", 12);
                SKILL_WEIGHTS.put("huggingface", 12);
                SKILL_WEIGHTS.put("mlops", 14);
                SKILL_WEIGHTS.put("apache spark", 14);
                SKILL_WEIGHTS.put("hadoop", 10);
                SKILL_WEIGHTS.put("airflow", 12);
                SKILL_WEIGHTS.put("dbt", 10);
                SKILL_WEIGHTS.put("dask", 8);
                SKILL_WEIGHTS.put("pyspark", 12);
                SKILL_WEIGHTS.put("data pipeline", 12);
                SKILL_WEIGHTS.put("etl", 12);
                SKILL_WEIGHTS.put("data modelling", 12);
                SKILL_WEIGHTS.put("data governance", 10);
                SKILL_WEIGHTS.put("databricks", 12);
                SKILL_WEIGHTS.put("looker", 10);
                SKILL_WEIGHTS.put("time series", 10);
                SKILL_WEIGHTS.put("forecasting", 10);
                SKILL_WEIGHTS.put("azure data factory", 10);
                SKILL_WEIGHTS.put("opencv", 10);

                // Security
                SKILL_WEIGHTS.put("cybersecurity", 15);
                SKILL_WEIGHTS.put("penetration testing", 15);
                SKILL_WEIGHTS.put("siem", 12);
                SKILL_WEIGHTS.put("firewall", 10);
                SKILL_WEIGHTS.put("encryption", 10);
                SKILL_WEIGHTS.put("vulnerability assessment", 12);
                SKILL_WEIGHTS.put("ethical hacking", 12);
                SKILL_WEIGHTS.put("owasp", 10);
                SKILL_WEIGHTS.put("network security", 12);
                SKILL_WEIGHTS.put("soc", 12);
                SKILL_WEIGHTS.put("incident response", 12);
                SKILL_WEIGHTS.put("devsecops", 12);
                SKILL_WEIGHTS.put("zero trust", 10);
                SKILL_WEIGHTS.put("iam", 10);

                // Mobile
                SKILL_WEIGHTS.put("android", 12);
                SKILL_WEIGHTS.put("ios", 12);
                SKILL_WEIGHTS.put("xcode", 8);
                SKILL_WEIGHTS.put("android studio", 8);

                // QA / Testing
                SKILL_WEIGHTS.put("selenium", 12);
                SKILL_WEIGHTS.put("junit", 10);
                SKILL_WEIGHTS.put("testng", 10);
                SKILL_WEIGHTS.put("jmeter", 10);
                SKILL_WEIGHTS.put("postman", 8);
                SKILL_WEIGHTS.put("api testing", 10);
                SKILL_WEIGHTS.put("test automation", 12);
                SKILL_WEIGHTS.put("test planning", 10);
                SKILL_WEIGHTS.put("manual testing", 8);
                SKILL_WEIGHTS.put("performance testing", 10);
                SKILL_WEIGHTS.put("cypress", 10);
                SKILL_WEIGHTS.put("playwright", 10);
                SKILL_WEIGHTS.put("appium", 10);
                SKILL_WEIGHTS.put("pytest", 10);
                SKILL_WEIGHTS.put("cucumber", 8);
                SKILL_WEIGHTS.put("bdd", 8);

                // IT / Sysadmin
                SKILL_WEIGHTS.put("windows server", 10);
                SKILL_WEIGHTS.put("active directory", 10);
                SKILL_WEIGHTS.put("system administration", 12);
                SKILL_WEIGHTS.put("troubleshooting", 8);
                SKILL_WEIGHTS.put("itil", 10);
                SKILL_WEIGHTS.put("service desk", 8);
                SKILL_WEIGHTS.put("vmware", 10);
                SKILL_WEIGHTS.put("hyper-v", 8);
                SKILL_WEIGHTS.put("office 365", 8);
                SKILL_WEIGHTS.put("azure ad", 10);

                // Business / Management
                SKILL_WEIGHTS.put("project management", 14);
                SKILL_WEIGHTS.put("agile", 10);
                SKILL_WEIGHTS.put("scrum", 10);
                SKILL_WEIGHTS.put("jira", 8);
                SKILL_WEIGHTS.put("communication", 8);
                SKILL_WEIGHTS.put("leadership", 8);
                SKILL_WEIGHTS.put("stakeholder management", 10);
                SKILL_WEIGHTS.put("business analysis", 12);
                SKILL_WEIGHTS.put("requirements gathering", 10);
                SKILL_WEIGHTS.put("product roadmap", 10);
                SKILL_WEIGHTS.put("product management", 14);
                SKILL_WEIGHTS.put("user research", 10);
                SKILL_WEIGHTS.put("market analysis", 10);
                SKILL_WEIGHTS.put("a/b testing", 10);
                SKILL_WEIGHTS.put("ux design", 12);
                SKILL_WEIGHTS.put("wireframing", 10);
                SKILL_WEIGHTS.put("prototyping", 10);
                SKILL_WEIGHTS.put("ui design", 10);
                SKILL_WEIGHTS.put("user testing", 10);
                SKILL_WEIGHTS.put("design systems", 10);
                SKILL_WEIGHTS.put("risk management", 10);
                SKILL_WEIGHTS.put("ms project", 8);
                SKILL_WEIGHTS.put("budget management", 10);
                SKILL_WEIGHTS.put("okr", 8);
                SKILL_WEIGHTS.put("kpi", 8);
                SKILL_WEIGHTS.put("salesforce", 10);
                SKILL_WEIGHTS.put("sap", 10);
                SKILL_WEIGHTS.put("erp", 10);
                SKILL_WEIGHTS.put("crm", 8);
                SKILL_WEIGHTS.put("kanban", 8);

                // Embedded / Blockchain / Game
                SKILL_WEIGHTS.put("rtos", 12);
                SKILL_WEIGHTS.put("arduino", 8);
                SKILL_WEIGHTS.put("raspberry pi", 8);
                SKILL_WEIGHTS.put("vhdl", 10);
                SKILL_WEIGHTS.put("verilog", 10);
                SKILL_WEIGHTS.put("microcontroller", 10);
                SKILL_WEIGHTS.put("fpga", 12);
                SKILL_WEIGHTS.put("can bus", 10);
                SKILL_WEIGHTS.put("pcb design", 10);
                SKILL_WEIGHTS.put("altium", 8);
                SKILL_WEIGHTS.put("blockchain", 12);
                SKILL_WEIGHTS.put("ethereum", 10);
                SKILL_WEIGHTS.put("web3", 10);
                SKILL_WEIGHTS.put("smart contracts", 12);
                SKILL_WEIGHTS.put("truffle", 8);
                SKILL_WEIGHTS.put("hardhat", 8);
                SKILL_WEIGHTS.put("ipfs", 8);
                SKILL_WEIGHTS.put("unity", 12);
                SKILL_WEIGHTS.put("unreal engine", 12);
                SKILL_WEIGHTS.put("opengl", 10);
                SKILL_WEIGHTS.put("blender", 8);
                SKILL_WEIGHTS.put("game physics", 8);
                SKILL_WEIGHTS.put("shaders", 8);

                // General
                SKILL_WEIGHTS.put("apache tika", 12);
                SKILL_WEIGHTS.put("thymeleaf", 8);
                SKILL_WEIGHTS.put("maven", 6);
                SKILL_WEIGHTS.put("gradle", 6);
                SKILL_WEIGHTS.put("excel", 8);
                SKILL_WEIGHTS.put("data structures", 10);
                SKILL_WEIGHTS.put("algorithms", 10);
        }

        // ── 40 Job Roles ─────────────────────────────────────────────────
        private static final Map<String, List<String>> JOB_SKILLS = new LinkedHashMap<>();
        static {
                // ── Software Development ──────────────────────────────────
                JOB_SKILLS.put("Full Stack Developer",
                                Arrays.asList("java", "spring boot", "react", "javascript", "html", "css", "mysql",
                                                "rest api", "git", "node.js", "typescript", "docker"));
                JOB_SKILLS.put("Backend Developer",
                                Arrays.asList("java", "spring boot", "mysql", "rest api", "microservices", "docker",
                                                "git", "postgresql", "redis", "kafka"));
                JOB_SKILLS.put("Frontend Developer",
                                Arrays.asList("html", "css", "javascript", "react", "typescript", "responsive design",
                                                "git", "rest api", "figma", "webpack", "tailwind"));
                JOB_SKILLS.put("Web Developer",
                                Arrays.asList("html", "css", "javascript", "react", "node.js", "responsive design",
                                                "git", "bootstrap", "rest api"));
                JOB_SKILLS.put("Software Engineer",
                                Arrays.asList("java", "python", "git", "sql", "rest api", "agile", "linux",
                                                "data structures", "algorithms", "docker"));
                JOB_SKILLS.put(".NET Developer",
                                Arrays.asList("c#", ".net", "asp.net", "sql", "azure", "rest api", "git",
                                                "microservices", "docker", "visual studio"));
                JOB_SKILLS.put("Node.js Developer",
                                Arrays.asList("node.js", "javascript", "typescript", "express.js", "rest api",
                                                "mongodb", "docker", "git", "redis", "rabbitmq"));
                JOB_SKILLS.put("Python Developer",
                                Arrays.asList("python", "django", "flask", "fastapi", "rest api", "postgresql",
                                                "docker", "git", "celery", "redis"));
                JOB_SKILLS.put("Java Developer",
                                Arrays.asList("java", "spring boot", "hibernate", "mysql", "rest api", "git",
                                                "microservices", "maven", "junit", "docker"));
                JOB_SKILLS.put("PHP Developer",
                                Arrays.asList("php", "laravel", "mysql", "javascript", "html", "css", "git", "rest api",
                                                "docker", "composer"));

                // ── Data & AI ────────────────────────────────────────────
                JOB_SKILLS.put("Data Scientist",
                                Arrays.asList("python", "machine learning", "deep learning", "statistics", "pandas",
                                                "numpy", "sql", "scikit-learn", "data visualization", "tensorflow",
                                                "r"));
                JOB_SKILLS.put("Data Analyst",
                                Arrays.asList("sql", "python", "data analysis", "tableau", "power bi", "statistics",
                                                "data visualization", "mysql", "excel", "looker"));
                JOB_SKILLS.put("Machine Learning Engineer",
                                Arrays.asList("python", "machine learning", "deep learning", "tensorflow", "pytorch",
                                                "scikit-learn", "feature engineering", "model deployment", "numpy",
                                                "pandas", "mlops"));
                JOB_SKILLS.put("AI Engineer",
                                Arrays.asList("python", "machine learning", "deep learning", "large language models",
                                                "tensorflow", "pytorch", "nlp", "prompt engineering", "rag",
                                                "langchain", "docker"));
                JOB_SKILLS.put("Data Engineer",
                                Arrays.asList("python", "apache spark", "hadoop", "airflow", "etl", "data pipeline",
                                                "sql", "aws", "bigquery", "snowflake", "dbt", "kafka"));
                JOB_SKILLS.put("Business Intelligence Developer",
                                Arrays.asList("sql", "power bi", "tableau", "data warehouse", "looker", "snowflake",
                                                "etl", "data modelling", "excel", "python"));
                JOB_SKILLS.put("NLP Engineer",
                                Arrays.asList("python", "nlp", "large language models", "pytorch", "huggingface",
                                                "langchain", "rag", "prompt engineering", "deep learning"));
                JOB_SKILLS.put("Computer Vision Engineer",
                                Arrays.asList("python", "computer vision", "deep learning", "pytorch", "tensorflow",
                                                "opencv", "scikit-learn", "docker"));

                // ── Cloud & DevOps ───────────────────────────────────────
                JOB_SKILLS.put("DevOps Engineer",
                                Arrays.asList("docker", "kubernetes", "aws", "ci/cd", "linux", "terraform", "jenkins",
                                                "ansible", "git", "shell scripting", "helm", "grafana"));
                JOB_SKILLS.put("Cloud Engineer",
                                Arrays.asList("aws", "azure", "gcp", "networking", "virtualization", "docker",
                                                "kubernetes", "terraform", "linux", "cloud architecture",
                                                "cloudformation"));
                JOB_SKILLS.put("Site Reliability Engineer",
                                Arrays.asList("linux", "kubernetes", "docker", "prometheus", "grafana", "ci/cd",
                                                "python", "bash", "aws", "incident response", "elk stack"));
                JOB_SKILLS.put("Cloud Architect",
                                Arrays.asList("aws", "azure", "gcp", "cloud architecture", "terraform", "microservices",
                                                "networking", "serverless", "kubernetes", "cloudformation"));
                JOB_SKILLS.put("Platform Engineer",
                                Arrays.asList("kubernetes", "docker", "terraform", "helm", "ci/cd", "aws", "linux",
                                                "python", "go", "github actions"));

                // ── Security ────────────────────────────────────────────
                JOB_SKILLS.put("Cybersecurity Analyst",
                                Arrays.asList("cybersecurity", "penetration testing", "siem", "firewall", "encryption",
                                                "vulnerability assessment", "ethical hacking", "owasp",
                                                "network security", "linux", "incident response"));
                JOB_SKILLS.put("Security Engineer",
                                Arrays.asList("cybersecurity", "devsecops", "zero trust", "iam", "owasp",
                                                "penetration testing", "network security", "soc", "aws", "kubernetes"));

                // ── Mobile ──────────────────────────────────────────────
                JOB_SKILLS.put("Mobile App Developer",
                                Arrays.asList("android", "ios", "kotlin", "swift", "flutter", "react native",
                                                "rest api", "git", "android studio", "xcode"));
                JOB_SKILLS.put("Android Developer",
                                Arrays.asList("android", "kotlin", "java", "android studio", "rest api", "git",
                                                "firebase", "jetpack compose", "mvvm", "sqlite"));
                JOB_SKILLS.put("iOS Developer",
                                Arrays.asList("ios", "swift", "xcode", "swiftui", "rest api", "git", "firebase",
                                                "sqlite", "cocoapods", "mvvm"));

                // ── Design ──────────────────────────────────────────────
                JOB_SKILLS.put("UI/UX Designer",
                                Arrays.asList("figma", "adobe xd", "sketch", "ux design", "wireframing", "prototyping",
                                                "user research", "ui design", "user testing", "design systems",
                                                "invision"));
                JOB_SKILLS.put("Product Designer",
                                Arrays.asList("figma", "ux design", "user research", "prototyping", "design systems",
                                                "a/b testing", "wireframing", "ui design", "zeplin"));

                // ── QA / Testing ────────────────────────────────────────
                JOB_SKILLS.put("QA Engineer",
                                Arrays.asList("selenium", "junit", "testng", "jmeter", "postman", "api testing",
                                                "test automation", "test planning", "manual testing",
                                                "performance testing", "cypress"));
                JOB_SKILLS.put("Automation Test Engineer",
                                Arrays.asList("selenium", "cypress", "playwright", "appium", "pytest", "rest api",
                                                "ci/cd", "git", "bdd", "cucumber", "java", "python"));

                // ── IT / Infra ───────────────────────────────────────────
                JOB_SKILLS.put("System Administrator",
                                Arrays.asList("linux", "windows server", "active directory", "networking",
                                                "shell scripting", "virtualization", "system administration",
                                                "troubleshooting", "aws", "ansible"));
                JOB_SKILLS.put("Database Administrator",
                                Arrays.asList("mysql", "postgresql", "oracle", "sql", "database administration",
                                                "database design", "query optimization", "redis", "mongodb",
                                                "snowflake"));
                JOB_SKILLS.put("IT Support Engineer",
                                Arrays.asList("troubleshooting", "windows server", "active directory", "networking",
                                                "office 365", "itil", "service desk", "azure ad", "vmware",
                                                "communication"));

                // ── Business / Product ───────────────────────────────────
                JOB_SKILLS.put("Business Analyst",
                                Arrays.asList("business analysis", "requirements gathering", "sql", "data analysis",
                                                "agile", "jira", "stakeholder management", "data modelling", "tableau",
                                                "communication"));
                JOB_SKILLS.put("Project Manager",
                                Arrays.asList("project management", "agile", "scrum", "jira", "communication",
                                                "leadership", "stakeholder management", "risk management", "ms project",
                                                "budget management"));
                JOB_SKILLS.put("Product Manager",
                                Arrays.asList("product management", "product roadmap", "user research",
                                                "market analysis", "agile", "a/b testing", "jira",
                                                "stakeholder management", "data analysis", "communication", "okr"));
                JOB_SKILLS.put("Scrum Master",
                                Arrays.asList("scrum", "agile", "jira", "communication", "leadership", "kanban",
                                                "sprint planning", "conflict resolution", "servant leadership",
                                                "risk management"));

                // ── Embedded / Blockchain / Game ────────────────────────
                JOB_SKILLS.put("Embedded Systems Engineer",
                                Arrays.asList("embedded c", "c++", "rtos", "microcontroller", "arduino", "can bus",
                                                "linux", "fpga", "pcb design", "git"));
                JOB_SKILLS.put("Blockchain Developer",
                                Arrays.asList("blockchain", "solidity", "ethereum", "web3", "smart contracts",
                                                "javascript", "truffle", "hardhat", "ipfs", "python", "git"));
                JOB_SKILLS.put("Game Developer",
                                Arrays.asList("unity", "unreal engine", "c++", "c#", "opengl", "game physics",
                                                "shaders", "blender", "git"));
        }

        // ── Improvement suggestions map ────────────────────────────────────
        private static final Map<String, String> SKILL_SUGGESTIONS = new LinkedHashMap<>();
        static {
                SKILL_SUGGESTIONS.put("java", "Learn Java Core and OOP, then build REST APIs with Spring Boot");
                SKILL_SUGGESTIONS.put("python",
                                "Practice Python on LeetCode and build real-world projects using automation or data tasks");
                SKILL_SUGGESTIONS.put("javascript",
                                "Build interactive UI projects with Vanilla JS, then explore ES6+ features");
                SKILL_SUGGESTIONS.put("typescript",
                                "Learn TypeScript basics, type system, and integrate it with React or Node.js projects");
                SKILL_SUGGESTIONS.put("c#", "Learn C# OOP, LINQ, and build web APIs with ASP.NET Core");
                SKILL_SUGGESTIONS.put(".net",
                                "Build a REST API with ASP.NET Core including auth, CRUD, and Entity Framework integration");
                SKILL_SUGGESTIONS.put("asp.net",
                                "Study ASP.NET MVC/Web API patterns and build a production-ready application");
                SKILL_SUGGESTIONS.put("kotlin", "Start Android development with Kotlin by building 2-3 simple apps");
                SKILL_SUGGESTIONS.put("swift",
                                "Learn Swift basics and build iOS apps using Xcode and deploy to TestFlight");
                SKILL_SUGGESTIONS.put("flutter",
                                "Build cross-platform mobile apps with Flutter and Dart — start with a todo or weather app");
                SKILL_SUGGESTIONS.put("react native",
                                "Learn React Native and build 2 production-style cross-platform apps");
                SKILL_SUGGESTIONS.put("go",
                                "Learn Go basics and build a REST API server — Go is great for cloud-native backends");
                SKILL_SUGGESTIONS.put("rust", "Study Rust's ownership model and build a small systems utility project");
                SKILL_SUGGESTIONS.put("php", "Learn PHP 8 features and build web apps using Laravel framework");
                SKILL_SUGGESTIONS.put("spring boot",
                                "Build a full REST API with Spring Boot including auth, CRUD, and database integration");
                SKILL_SUGGESTIONS.put("react",
                                "Build frontend projects using React — try a portfolio site or dashboard and deploy to Vercel");
                SKILL_SUGGESTIONS.put("angular",
                                "Complete Angular's official tour-of-heroes tutorial, then build a real CRUD app");
                SKILL_SUGGESTIONS.put("vue",
                                "Learn Vue.js basics and build a dynamic SPA with Vue Router and Vuex state management");
                SKILL_SUGGESTIONS.put("node.js",
                                "Build a Node.js REST API with Express.js, connect to MongoDB, and deploy on Heroku");
                SKILL_SUGGESTIONS.put("django",
                                "Build a web app with Django including auth, ORM, and REST API using DRF");
                SKILL_SUGGESTIONS.put("flask",
                                "Create a Python Flask API with database integration and deploy it on a cloud platform");
                SKILL_SUGGESTIONS.put("fastapi",
                                "Learn FastAPI to build modern async REST APIs in Python with auto-generated docs");
                SKILL_SUGGESTIONS.put("next.js",
                                "Build a full-stack app with Next.js using SSR, API routes, and deploy on Vercel");
                SKILL_SUGGESTIONS.put("html",
                                "Practice HTML5 semantic tags by building real websites — focus on accessibility");
                SKILL_SUGGESTIONS.put("css",
                                "Master CSS Flexbox, Grid, and animations — rebuild popular website UIs for practice");
                SKILL_SUGGESTIONS.put("responsive design",
                                "Study CSS media queries and Flexbox/Grid to build fully responsive layouts from scratch");
                SKILL_SUGGESTIONS.put("graphql",
                                "Learn GraphQL by building a schema, resolvers, and mutations — replace a REST endpoint");
                SKILL_SUGGESTIONS.put("sql",
                                "Improve SQL by solving 50+ queries on LeetCode/HackerRank — practice JOINs, subqueries, and window functions");
                SKILL_SUGGESTIONS.put("mysql",
                                "Set up MySQL locally and practise CRUD, indexing, stored procedures, and transactions");
                SKILL_SUGGESTIONS.put("postgresql",
                                "Learn PostgreSQL-specific features like JSONB, window functions, and advanced indexing");
                SKILL_SUGGESTIONS.put("mongodb",
                                "Learn MongoDB CRUD, aggregation pipeline, and indexing — build a project using Atlas free tier");
                SKILL_SUGGESTIONS.put("redis",
                                "Learn Redis for caching and session management — integrate with a Spring Boot or Node.js app");
                SKILL_SUGGESTIONS.put("snowflake",
                                "Complete Snowflake's free hands-on labs and practice data warehousing concepts");
                SKILL_SUGGESTIONS.put("bigquery",
                                "Learn BigQuery SQL and practice analysing large public datasets on Google Cloud");
                SKILL_SUGGESTIONS.put("docker",
                                "Learn Docker containerisation basics and Dockerise an existing project with a multi-stage build");
                SKILL_SUGGESTIONS.put("kubernetes",
                                "Deploy a containerised app to a local Minikube cluster then to a managed cloud service");
                SKILL_SUGGESTIONS.put("aws",
                                "Get AWS Cloud Practitioner certified — practice with EC2, S3, Lambda, and RDS on the free tier");
                SKILL_SUGGESTIONS.put("azure",
                                "Complete AZ-900 Azure Fundamentals and deploy an app on Azure App Service");
                SKILL_SUGGESTIONS.put("gcp",
                                "Explore Google Cloud on Qwiklabs and earn the Associate Cloud Engineer certification");
                SKILL_SUGGESTIONS.put("ci/cd",
                                "Set up a CI/CD pipeline using GitHub Actions or Jenkins to automate build, test, and deploy");
                SKILL_SUGGESTIONS.put("terraform",
                                "Learn Infrastructure as Code with Terraform — provision AWS resources using Terraform scripts");
                SKILL_SUGGESTIONS.put("ansible",
                                "Learn Ansible playbooks to automate server configuration and application deployment");
                SKILL_SUGGESTIONS.put("linux",
                                "Practice Linux command-line skills daily — learn process, file, and network management on Ubuntu");
                SKILL_SUGGESTIONS.put("git",
                                "Master Git branching strategies (Gitflow), rebasing, and collaborative workflows on GitHub");
                SKILL_SUGGESTIONS.put("helm",
                                "Learn Helm chart creation and management to package and deploy Kubernetes applications");
                SKILL_SUGGESTIONS.put("machine learning",
                                "Learn Machine Learning using Python and Scikit-learn and build 2 end-to-end ML projects");
                SKILL_SUGGESTIONS.put("deep learning",
                                "Study Deep Learning with TensorFlow or PyTorch — build image classification and NLP projects");
                SKILL_SUGGESTIONS.put("tensorflow",
                                "Complete the TensorFlow Developer certification and build models for real-world datasets");
                SKILL_SUGGESTIONS.put("pytorch",
                                "Learn PyTorch by implementing models — start with image classification on CIFAR-10");
                SKILL_SUGGESTIONS.put("scikit-learn",
                                "Build ML pipelines with Scikit-learn including preprocessing, model selection, and evaluation");
                SKILL_SUGGESTIONS.put("pandas",
                                "Practise Pandas data manipulation with Kaggle datasets — master groupby, merge, and pivot tables");
                SKILL_SUGGESTIONS.put("numpy",
                                "Study NumPy arrays, broadcasting, and linear algebra operations through practice exercises");
                SKILL_SUGGESTIONS.put("statistics",
                                "Learn probability, hypothesis testing, and regression analysis — apply them to real datasets");
                SKILL_SUGGESTIONS.put("data analysis",
                                "Practise data analysis on Kaggle — work through full EDA pipeline on 3 different datasets");
                SKILL_SUGGESTIONS.put("data visualization",
                                "Learn Matplotlib, Seaborn, and Plotly to create insightful charts and dashboards");
                SKILL_SUGGESTIONS.put("tableau",
                                "Learn Tableau through free public training and build 3 interactive dashboards in Tableau Public");
                SKILL_SUGGESTIONS.put("power bi",
                                "Complete Microsoft's free Power BI learning path and build a business dashboard with real data");
                SKILL_SUGGESTIONS.put("nlp",
                                "Study NLP basics — implement text classification, sentiment analysis, and build a chatbot");
                SKILL_SUGGESTIONS.put("large language models",
                                "Learn LLM fundamentals, explore OpenAI/HuggingFace APIs, and build an LLM-powered app");
                SKILL_SUGGESTIONS.put("prompt engineering",
                                "Practise prompt engineering techniques — few-shot, chain-of-thought, and RAG patterns");
                SKILL_SUGGESTIONS.put("rag",
                                "Study Retrieval-Augmented Generation — build a QA system over your documents using LangChain");
                SKILL_SUGGESTIONS.put("langchain",
                                "Build LLM-powered apps using LangChain — chains, agents, memory, and tool use");
                SKILL_SUGGESTIONS.put("mlops",
                                "Learn MLflow and Kubeflow for experiment tracking, model registry, and serving pipelines");
                SKILL_SUGGESTIONS.put("apache spark",
                                "Learn Spark RDD/DataFrame APIs and process large datasets using PySpark on Databricks");
                SKILL_SUGGESTIONS.put("airflow",
                                "Build data pipelines using Apache Airflow DAGs — integrate with cloud storage and databases");
                SKILL_SUGGESTIONS.put("dbt",
                                "Learn dbt for data transformations in the warehouse — build models, tests, and documentation");
                SKILL_SUGGESTIONS.put("etl",
                                "Design and implement ETL pipelines using Python and cloud-native tools like AWS Glue");
                SKILL_SUGGESTIONS.put("cybersecurity",
                                "Get CompTIA Security+ certified and set up a home lab to practise security techniques");
                SKILL_SUGGESTIONS.put("penetration testing",
                                "Learn ethical hacking on TryHackMe or HackTheBox and earn CEH or OSCP certification");
                SKILL_SUGGESTIONS.put("vulnerability assessment",
                                "Learn vulnerability scanning using Nessus, OpenVAS, and Nmap with real targets");
                SKILL_SUGGESTIONS.put("ethical hacking",
                                "Start on TryHackMe — complete learning paths in web fundamentals and penetration testing");
                SKILL_SUGGESTIONS.put("network security",
                                "Study OSI model, firewalls, IDS/IPS, and practise packet analysis with Wireshark");
                SKILL_SUGGESTIONS.put("siem",
                                "Learn SIEM basics with Splunk or IBM QRadar — practise log analysis and alert correlation");
                SKILL_SUGGESTIONS.put("devsecops",
                                "Integrate security into your CI/CD pipeline — learn SAST, DAST, and container scanning tools");
                SKILL_SUGGESTIONS.put("figma",
                                "Learn Figma by redesigning a popular app — focus on components, auto-layout, and prototyping");
                SKILL_SUGGESTIONS.put("adobe xd",
                                "Complete Adobe XD tutorials and design a mobile app prototype with interactive clickthroughs");
                SKILL_SUGGESTIONS.put("ux design",
                                "Study the UX design process — user research, personas, journey maps, and usability testing");
                SKILL_SUGGESTIONS.put("wireframing",
                                "Create wireframes for 3 app concepts — practise low-fi and hi-fi in Figma or Balsamiq");
                SKILL_SUGGESTIONS.put("prototyping",
                                "Build interactive prototypes in Figma and conduct user testing sessions to gather feedback");
                SKILL_SUGGESTIONS.put("user research",
                                "Learn user interview, survey, and card-sorting techniques to inform data-driven design decisions");
                SKILL_SUGGESTIONS.put("selenium",
                                "Build end-to-end test suites using Selenium WebDriver with Java or Python for a real web app");
                SKILL_SUGGESTIONS.put("cypress",
                                "Build a Cypress E2E test suite for a web app — include CI integration with GitHub Actions");
                SKILL_SUGGESTIONS.put("playwright",
                                "Write cross-browser tests with Playwright and integrate them into a CI/CD pipeline");
                SKILL_SUGGESTIONS.put("test automation",
                                "Learn TestNG or Pytest frameworks and automate a full regression test suite");
                SKILL_SUGGESTIONS.put("api testing",
                                "Learn REST API testing using Postman and automate with RestAssured or pytest");
                SKILL_SUGGESTIONS.put("jmeter",
                                "Learn Apache JMeter to create performance test plans and analyse throughput and response times");
                SKILL_SUGGESTIONS.put("project management",
                                "Earn PMP or PRINCE2 Foundation and practise managing a real side-project");
                SKILL_SUGGESTIONS.put("agile",
                                "Study Agile Scrum framework — consider Scrum Master (PSM) certification");
                SKILL_SUGGESTIONS.put("scrum",
                                "Learn the Scrum Guide and practise facilitation of sprint ceremonies in your team");
                SKILL_SUGGESTIONS.put("business analysis",
                                "Learn the BABOK framework and practise requirements gathering and use-case modelling");
                SKILL_SUGGESTIONS.put("product management",
                                "Study product management frameworks (RICE, OKRs) and build a product case study from scratch");
                SKILL_SUGGESTIONS.put("product roadmap",
                                "Practise building product roadmaps in tools like Aha! or Notion aligned with user goals");
                SKILL_SUGGESTIONS.put("market analysis",
                                "Learn competitive analysis and market sizing — apply them to a product you use daily");
                SKILL_SUGGESTIONS.put("database administration",
                                "Learn MySQL/PostgreSQL DBA tasks — backup, recovery, replication, and performance tuning");
                SKILL_SUGGESTIONS.put("database design",
                                "Practise ER diagram design, normalisation to 3NF, and build schemas for real-world use cases");
                SKILL_SUGGESTIONS.put("query optimization",
                                "Study query execution plans and indexing strategies to optimise slow database queries");
                SKILL_SUGGESTIONS.put("networking",
                                "Study CCNA curriculum — TCP/IP, routing protocols, and subnetting with Packet Tracer");
                SKILL_SUGGESTIONS.put("shell scripting",
                                "Write Bash scripts to automate repetitive Linux tasks — cron jobs, file processing, monitoring");
                SKILL_SUGGESTIONS.put("android",
                                "Build 2 Android apps in Kotlin using Android Studio — publish one to the Google Play Store");
                SKILL_SUGGESTIONS.put("ios",
                                "Build 2 iOS apps in Swift using Xcode — submit one to Apple TestFlight for testing");
                SKILL_SUGGESTIONS.put("swiftui",
                                "Rebuild an existing UIKit app using SwiftUI to learn declarative UI patterns");
                SKILL_SUGGESTIONS.put("jetpack compose",
                                "Build an Android app with Jetpack Compose — explore state, theming, and navigation");
                SKILL_SUGGESTIONS.put("microservices",
                                "Build a microservices architecture with Spring Boot — include service discovery and API gateway");
                SKILL_SUGGESTIONS.put("rest api",
                                "Design and implement a RESTful API following best practices — versioning, auth, and documentation");
                SKILL_SUGGESTIONS.put("stakeholder management",
                                "Learn communication and negotiation techniques for managing stakeholder expectations");
                SKILL_SUGGESTIONS.put("leadership",
                                "Take on team lead responsibilities and practise delegation, mentoring, and conflict resolution");
                SKILL_SUGGESTIONS.put("communication",
                                "Improve presentation and written communication — practise technical writing and stakeholder updates");
                SKILL_SUGGESTIONS.put("data structures",
                                "Study arrays, linked lists, trees, graphs, and heaps — practise 50 problems on LeetCode");
                SKILL_SUGGESTIONS.put("algorithms",
                                "Study sorting, searching, dynamic programming, and graph algorithms on LeetCode/Codeforces");
                SKILL_SUGGESTIONS.put("excel",
                                "Learn advanced Excel — VLOOKUP, pivot tables, macros, and Power Query for data analysis");
                SKILL_SUGGESTIONS.put("risk management",
                                "Study risk identification, assessment matrices, and mitigation planning in project management");
                SKILL_SUGGESTIONS.put("embedded c",
                                "Practice embedded C by implementing device drivers on Arduino or STM32 microcontrollers");
                SKILL_SUGGESTIONS.put("rtos",
                                "Study FreeRTOS task scheduling, semaphores, and queues — build a real-time sensor project");
                SKILL_SUGGESTIONS.put("blockchain",
                                "Learn Ethereum fundamentals, Solidity, and build and deploy a smart contract on a testnet");
                SKILL_SUGGESTIONS.put("solidity",
                                "Build 2 smart contracts in Solidity using Hardhat — include tests and deploy to a testnet");
                SKILL_SUGGESTIONS.put("web3",
                                "Learn Web3.js/ethers.js and build a simple DApp that interacts with deployed smart contracts");
                SKILL_SUGGESTIONS.put("unity",
                                "Build a 2D or 3D game in Unity — publish it to itch.io and document your development process");
                SKILL_SUGGESTIONS.put("unreal engine",
                                "Complete Unreal Engine's Blueprints Visual Scripting course and build a small playable level");
                SKILL_SUGGESTIONS.put("computer vision",
                                "Build an OpenCV project for object detection or face recognition using a pre-trained model");
                SKILL_SUGGESTIONS.put("feature engineering",
                                "Practise feature selection, creation, and transformation techniques on Kaggle datasets");
                SKILL_SUGGESTIONS.put("model deployment",
                                "Learn to deploy ML models using Flask/FastAPI, Docker, and cloud services like AWS SageMaker");
                SKILL_SUGGESTIONS.put("cloud architecture",
                                "Study cloud design patterns — high availability, fault tolerance, and scalability on AWS/Azure");
                SKILL_SUGGESTIONS.put("kafka",
                                "Build a producer-consumer pipeline with Apache Kafka — integrate with a Spring Boot or Python app");
                SKILL_SUGGESTIONS.put("databricks",
                                "Complete Databricks' free learning path and build a Spark pipeline on the Community Edition");
                SKILL_SUGGESTIONS.put("data warehouse",
                                "Learn dimensional modelling and build a star schema data warehouse for a use case");
                SKILL_SUGGESTIONS.put("data pipeline",
                                "Build an end-to-end data pipeline from ingestion to dashboard using open-source tools");
        }

        // ── Public API ────────────────────────────────────────────────────

        public List<String> getJobTitles() {
                return new ArrayList<>(JOB_SKILLS.keySet());
        }

        public void analyseResume(Resume resume, String jobTitle, String jobDesc) {
                // generate whyScore, profileSummary, hiringDecision and improvementPlan here
                String raw = resume.getRawText() == null ? "" : resume.getRawText().toLowerCase();
                List<String> detected = detectSkills(raw);
                resume.setSkills(joinPipe(detected));
                resume.setExperience(detectExperienceLevel(raw));

                int expMatch = 0;
                String exp = resume.getExperience();
                if (exp != null) {
                        String lowerExp = exp.toLowerCase();
                        if (lowerExp.contains("fresher"))
                                expMatch = 40;
                        else if (lowerExp.contains("junior"))
                                expMatch = 60;
                        else if (lowerExp.contains("mid"))
                                expMatch = 80;
                        else if (lowerExp.contains("senior"))
                                expMatch = 90;
                }

                if (jobTitle != null && !jobTitle.isBlank() && JOB_SKILLS.containsKey(jobTitle)) {
                        List<String> required = JOB_SKILLS.get(jobTitle);
                        List<String> matched = new ArrayList<>();
                        List<String> missing = new ArrayList<>();
                        for (String skill : required) {
                                if (detected.contains(skill))
                                        matched.add(skill);
                                else
                                        missing.add(skill);
                        }
                        int skillsMatchPct = required.isEmpty() ? 0 : (matched.size() * 100) / required.size();
                        int kwMatchPct = Math.min(100, detected.size() * 5);

                        // Set detailed breakdown fields first
                        resume.setSkillsMatch(skillsMatchPct);
                        resume.setKeywordMatch(kwMatchPct);
                        resume.setExperienceMatch(expMatch);

                        // Combine into overall pct (Skills=50%, Keywords=30%, Experience=20%)
                        int combinedPct = (int) Math
                                        .round((skillsMatchPct * 0.5) + (kwMatchPct * 0.3) + (expMatch * 0.2));
                        if (jobDesc != null && !jobDesc.isBlank()) {
                                combinedPct = Math.min(100, combinedPct + cosineBoost(raw, jobDesc.toLowerCase()));
                        }

                        // Set basic fields
                        int rawScore = scoreSkills(matched);
                        resume.setMatchPercentage(combinedPct);
                        resume.setScore(rawScore + detected.size());
                        resume.setMatchedSkills(joinPipe(matched));
                        resume.setMissingSkills(joinPipe(missing));

                        // Detailed ATS rationale sections
                        resume.setWhyScore(joinPipe(buildWhyScore(required, matched, missing, expMatch, combinedPct)));
                        resume.setProfileSummary(buildProfileSummary(matched, missing, expMatch));
                        resume.setHiringDecision(decideHiring(combinedPct));
                        resume.setHiringImpact(buildHiringImpact(combinedPct, missing.size(), expMatch));
                        resume.setImprovementPlan(joinPipe(buildImprovementPlan(missing, required)));
                        resume.setRecruiterNote("Candidate requires improvement in " + missing.size()
                                        + " key areas before being considered for this role.");
                        resume.setOneLineSummary(buildOneLineSummary(combinedPct));

                        // Suggestions logic
                        List<String> suggestions = missing.stream()
                                        .filter(SKILL_SUGGESTIONS::containsKey)
                                        .map(SKILL_SUGGESTIONS::get)
                                        .limit(10)
                                        .collect(Collectors.toList());

                        if (missing.size() >= 5) {
                                suggestions.add(0,
                                                "⚠️ Warning: You are missing several critical skills for this role.");
                        }
                        if (combinedPct < 60) {
                                suggestions.add("⚠️ Important: Consider building real-world projects and demonstrating hands-on experience to improve your fit.");
                        }

                        // Generate fallback generic suggestions if under 5
                        List<String> genericSuggestions = Arrays.asList(
                                        "Enhance your resume by quantifying your achievements with metrics.",
                                        "Tailor your resume specifically for the job role to highlight relevant experience.",
                                        "Consider taking online certifications to strengthen your technical background.",
                                        "Update your LinkedIn profile alongside your resume for better visibility.",
                                        "Include a portfolio or link to your GitHub to demonstrate your practical work.");

                        for (String genSugg : genericSuggestions) {
                                if (suggestions.size() >= 5)
                                        break;
                                if (!suggestions.contains(genSugg)) {
                                        suggestions.add(genSugg);
                                }
                        }

                        resume.setSuggestions(joinPipe(suggestions));

                        if (combinedPct < 50) {
                                resume.setRecommendedRoles(joinPipe(findBestMatchingRoles(detected, jobTitle, 3)));
                        } else {
                                resume.setRecommendedRoles("");
                        }
                        resume.setSuggestedJobs("");
                } else {
                        resume.setScore(scoreSkills(detected));
                        resume.setMatchPercentage(0);

                        resume.setSkillsMatch(0);
                        resume.setKeywordMatch(Math.min(100, detected.size() * 5));
                        resume.setExperienceMatch(expMatch);

                        resume.setMatchedSkills("");
                        resume.setMissingSkills("");
                        resume.setSuggestions("");
                        resume.setRecommendedRoles("");
                        resume.setSuggestedJobs(joinPipe(findBestMatchingRoles(detected, null, 4)));

                        resume.setWhyScore("Job role not selected. Provide a target role to generate detailed ATS analysis.");
                        resume.setProfileSummary("Role-specific profile summary is available after selecting a target job title.");
                        resume.setHiringDecision("Needs Improvement");
                        resume.setHiringImpact("Cannot make a hiring recommendation until a target role is selected.");
                        resume.setImprovementPlan("");
                        resume.setRecruiterNote("Candidate requires improvement in role targeting before formal evaluation.");
                        resume.setOneLineSummary("Not suitable for current role.");
                }
        }

        // ── Private helpers ───────────────────────────────────────────────

        private List<String> detectSkills(String rawLower) {
                List<String> found = new ArrayList<>();
                for (String skill : SKILL_WEIGHTS.keySet()) {
                        if (rawLower.contains(skill))
                                found.add(skill);
                }
                return found;
        }

        private int scoreSkills(List<String> skills) {
                int total = 0;
                for (String s : skills)
                        total += SKILL_WEIGHTS.getOrDefault(s, 5);
                return total;
        }

        private String detectExperienceLevel(String rawLower) {
                int maxYears = 0;
                String[] tokens = rawLower.split("\\s+");
                for (int i = 0; i < tokens.length - 1; i++) {
                        try {
                                int n = Integer.parseInt(tokens[i].replaceAll("[^0-9]", ""));
                                String next = tokens[i + 1];
                                if (next.startsWith("year") || next.startsWith("yr")) {
                                        if (n > maxYears)
                                                maxYears = n;
                                }
                        } catch (NumberFormatException ignored) {
                        }
                }
                if (maxYears == 0) {
                        if (rawLower.contains("fresher") || rawLower.contains("entry level")
                                        || rawLower.contains("intern"))
                                return "Fresher";
                        if (rawLower.contains("senior") || rawLower.contains("lead") || rawLower.contains("principal")
                                        || rawLower.contains("architect"))
                                return "Senior (5+ yrs)";
                        return "Not specified";
                }
                if (maxYears <= 2)
                        return maxYears + " yr(s) — Junior";
                if (maxYears <= 5)
                        return maxYears + " yrs — Mid-level";
                return maxYears + " yrs — Senior";
        }

        private int cosineBoost(String resumeText, String jdText) {
                Set<String> resumeWords = new HashSet<>(Arrays.asList(resumeText.split("\\W+")));
                Set<String> jdWords = new HashSet<>(Arrays.asList(jdText.split("\\W+")));
                jdWords.retainAll(resumeWords);
                return Math.min(10, jdWords.size() / 10);
        }

        private List<String> findBestMatchingRoles(List<String> detected, String excludeTitle, int limit) {
                Map<String, Integer> roleScores = new LinkedHashMap<>();
                for (Map.Entry<String, List<String>> entry : JOB_SKILLS.entrySet()) {
                        if (entry.getKey().equals(excludeTitle))
                                continue;
                        List<String> intersection = new ArrayList<>(detected);
                        intersection.retainAll(entry.getValue());
                        if (!intersection.isEmpty())
                                roleScores.put(entry.getKey(), intersection.size());
                }
                return roleScores.entrySet().stream()
                                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                                .limit(limit)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());
        }

        private List<String> buildWhyScore(List<String> required, List<String> matched, List<String> missing,
                        int expMatch, int overallMatch) {
                List<String> why = new ArrayList<>();
                why.add("Candidate matches " + matched.size() + " out of " + required.size() + " required skills.");

                if (!missing.isEmpty()) {
                        List<String> criticalMissing = missing.stream()
                                        .sorted((a, b) -> Integer.compare(SKILL_WEIGHTS.getOrDefault(b, 0),
                                                        SKILL_WEIGHTS.getOrDefault(a, 0)))
                                        .limit(3)
                                        .map(this::formatSkill)
                                        .collect(Collectors.toList());
                        why.add("Missing critical skills: " + String.join(", ", criticalMissing) + ".");
                } else {
                        why.add("No critical skill gaps detected for this role.");
                }

                if (expMatch < 60) {
                        why.add("Experience level appears below the expected level for this role.");
                } else if (expMatch < 80) {
                        why.add("Experience alignment is moderate; candidate may need guided onboarding.");
                } else {
                        why.add("Experience alignment is strong for role expectations.");
                }

                if (overallMatch >= 80) {
                        why.add("Overall profile shows strong readiness for interview progression.");
                } else if (overallMatch >= 60) {
                        why.add("Overall profile is moderate and needs targeted skill upgrades.");
                } else {
                        why.add("Overall profile currently falls short of direct hiring threshold.");
                }
                return why;
        }

        private String buildProfileSummary(List<String> matched, List<String> missing, int expMatch) {
                String strengthText = matched.isEmpty()
                                ? "limited direct role-specific strengths"
                                : "a foundation in " + matched.stream().limit(4).map(this::formatSkill)
                                                .collect(Collectors.joining(", "));
                String gapText = missing.isEmpty()
                                ? "no major missing skills"
                                : "gaps in " + missing.stream().limit(3).map(this::formatSkill)
                                                .collect(Collectors.joining(", "));

                String expText = expMatch >= 80 ? "experience readiness is strong"
                                : (expMatch >= 60 ? "experience readiness is moderate"
                                                : "experience readiness is currently low");

                return "Candidate shows " + strengthText + " but has " + gapText + "; " + expText + ".";
        }

        private String decideHiring(int overallMatch) {
                if (overallMatch > 80) {
                        return "Strong Candidate";
                }
                if (overallMatch > 60) {
                        return "Moderate Fit";
                }
                return "Needs Improvement";
        }

        private String buildOneLineSummary(int overallMatch) {
                if (overallMatch > 80) {
                        return "Highly recommended candidate.";
                }
                if (overallMatch > 60) {
                        return "Potential candidate with improvements needed.";
                }
                return "Not suitable for current role.";
        }

        private String buildHiringImpact(int overallMatch, int missingCount, int expMatch) {
                if (overallMatch > 80) {
                        return "Good fit for junior role with low onboarding risk.";
                }
                if (overallMatch >= 60) {
                        return "Can be considered for internship or junior pipeline after targeted improvement.";
                }
                if (missingCount >= 5 || expMatch < 60) {
                        return "Not suitable for immediate hiring; revisit after structured upskilling.";
                }
                return "Potential future candidate if core gaps are addressed through focused learning.";
        }

        private List<String> buildImprovementPlan(List<String> missing, List<String> required) {
                List<String> plan = new ArrayList<>();
                List<String> prioritized = missing.stream()
                                .sorted((a, b) -> Integer.compare(SKILL_WEIGHTS.getOrDefault(b, 0),
                                                SKILL_WEIGHTS.getOrDefault(a, 0)))
                                .limit(5)
                                .collect(Collectors.toList());

                for (String skill : prioritized) {
                        String titleSkill = formatSkill(skill);
                        String why = required.contains(skill)
                                        ? "Required for this target role"
                                        : "Frequently expected in similar job descriptions";
                        String how = toActionPlan(skill);
                        plan.add(titleSkill + " -> " + why + " -> " + how);
                }

                if (plan.isEmpty()) {
                        plan.add("Portfolio Depth -> Helps validate practical readiness -> Build 2 production-style projects and publish on GitHub");
                }
                return plan;
        }

        private String toActionPlan(String skill) {
                String suggestion = SKILL_SUGGESTIONS.get(skill);
                if (suggestion == null || suggestion.isBlank()) {
                        return "Complete a focused mini-project and document it in your resume";
                }
                int splitAt = suggestion.indexOf(',');
                if (splitAt > 0 && splitAt < suggestion.length() - 1) {
                        return suggestion.substring(0, splitAt).trim();
                }
                return suggestion;
        }

        private String formatSkill(String skill) {
                if (skill == null || skill.isBlank()) {
                        return "Skill";
                }
                String[] parts = skill.split("\\s+");
                List<String> titled = new ArrayList<>();
                for (String part : parts) {
                        if (part.isBlank()) {
                                continue;
                        }
                        titled.add(part.substring(0, 1).toUpperCase() + part.substring(1));
                }
                return String.join(" ", titled);
        }

        private String joinPipe(List<String> list) {
                if (list == null || list.isEmpty())
                        return "";
                return String.join("||", list);
        }
}
