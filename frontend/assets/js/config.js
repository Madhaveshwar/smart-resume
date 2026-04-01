@GetMapping("/api/job-roles")
public List<String> getJobRoles() {
    return List.of(
        "Software Engineer",
        "Data Scientist",
        "Web Developer",
        "AI Engineer",
        "DevOps Engineer"
    );
}