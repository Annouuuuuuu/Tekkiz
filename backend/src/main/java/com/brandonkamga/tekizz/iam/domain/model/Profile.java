package com.brandonkamga.tekizz.iam.domain.model;

/**
 * Profile — part of the User aggregate.
 * Pure Java domain entity, zero JPA/Spring annotations.
 */
public class Profile {

    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String country;
    private String bio;

    public Profile() {}

    public Profile(Long id, Long userId, String firstName, String lastName,
                   String avatarUrl, String country, String bio) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarUrl = avatarUrl;
        this.country = country;
        this.bio = bio;
    }

    public static Profile create(Long userId, String firstName, String lastName) {
        Profile profile = new Profile();
        profile.userId = userId;
        profile.firstName = firstName;
        profile.lastName = lastName;
        return profile;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getCountry() { return country; }
    public String getBio() { return bio; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setCountry(String country) { this.country = country; }
    public void setBio(String bio) { this.bio = bio; }
}
