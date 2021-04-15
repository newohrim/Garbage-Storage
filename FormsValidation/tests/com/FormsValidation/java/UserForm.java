package com.FormsValidation.java;

import java.util.List;

@Constrained
public class UserForm {
    @NotNull
    @NotBlank
    @Size(min = 5, max = 12)
    private String login;
    @NotNull
    @NotBlank
    @Size(min = 5, max = 12)
    private String password;
    @Positive
    @InRange(min = 1L, max = 100L)
    private int points;
    @AnyOf({ "User", "Moderator", "Admin" })
    private String userRank;
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 10)
    private List<@NotNull @NotEmpty List<@NotBlank String>> comments;
    @Positive
    private int usersUpvotes;
    @Negative
    private int usersDownvotes;
    @NotNull
    private Unrelated garbageInfo;

    public UserForm(String login, String password, int points, String userRank, List<List<String>> comments, int usersUpvotes, int usersDownvotes, Unrelated garbageInfo)
    {
        this.login = login;
        this.password = password;
        this.points = points;
        this.userRank = userRank;
        this.comments = comments;
        this.usersUpvotes = usersUpvotes;
        this.usersDownvotes = usersDownvotes;
        this.garbageInfo = garbageInfo;
    }
}
