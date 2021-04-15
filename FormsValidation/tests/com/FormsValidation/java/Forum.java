package com.FormsValidation.java;

import java.util.List;
import java.util.Map;

@Constrained
public class Forum {
    @NotNull
    @NotEmpty
    private List<@NotNull UserForm> users;
    @Positive
    @InRange(min = 0L, max = 999L)
    private int usersCount;
    @NotNull
    @NotBlank
    @Size(min = 10, max = 69)
    private String url;
    @NotNull
    private UserForm adminsForm;
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5)
    private final Map<String, Integer> map = Map.of();
    @NotNull
    @Positive
    private static Integer StaticInt = -1;
    @NotNull
    @NotEmpty
    private final List<String> notAnnotatedStrings = List.of("test", "test", "test", "test");

    public Forum(List<UserForm> users, int usersCount, String url, UserForm adminsForm)
    {
        this.users = users;
        this.usersCount = usersCount;
        this.url = url;
        this.adminsForm = adminsForm;
    }
}
