package com.FormsValidation.java;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FormsValidatorTest {

    private static Validator validator = new FormsValidator();

    private void pathsTest(final Object form, final String... paths)
    {
        Set<ValidationError> validationErrors = validator.validate(form);
        //assertEquals(paths.length, validationErrors.size());
        HashSet<String> pathsErrs = new HashSet<String>();
        for (ValidationError err : validationErrors)
            pathsErrs.add(err.getPath());
        for (String path : paths)
            assertTrue(pathsErrs.contains(path));
    }

    @Test
    void validateBookingForm()
    {
        List<GuestForm> guests = List.of(
                new GuestForm(/*firstName*/ null, /*lastName*/ "Def", /*age*/ 21),
                new GuestForm(/*firstName*/ "", /*lastName*/ "Ijk", /*age*/ -3)
        );
        Unrelated unrelated = new Unrelated(-1);
        BookingForm bookingForm = new BookingForm(
                guests,
                /*amenities*/ List.of("TV", "Piano"),
                /*propertyType*/ "Apartment",
                unrelated
        );
        pathsTest(
                bookingForm,
                "guests[0].firstName",
                "guests[1].age",
                "guests[1].firstName",
                "amenities[1]",
                "propertyType"
        );
    }

    @Test
    void validateForumForm()
    {
        List<String> comments1 = List.of("-rep", "+rep", "", "Pog");
        List<String> comments2 = List.of("send nudes", "", "nice job", "kek");
        List<String> comments3 = null;
        List<List<String>> comments = new ArrayList<List<String>>();
        comments.add(comments1);
        comments.add(comments2);
        comments.add(comments3);
        UserForm user1 = new UserForm(
                "qwerty",
                "passwordpassword1234",
                -1,
                "SuperAdmin",
                comments,
                -100,
                100,
                new Unrelated(-100)
                );
        UserForm admin = new UserForm(
                "admin",
                "admin",
                100,
                "Admin",
                null,
                Integer.MAX_VALUE,
                Integer.MIN_VALUE,
                null
        );
        Forum forum = new Forum(List.of(user1, admin), 2, "memes.org", admin);
        pathsTest(forum,
                "users[0].comments[0][2]",
                "users[0].comments[1][1]",
                "users[0].comments[2]",
                "users[0].password",
                "users[0].points",
                "users[0].userRank",
                "users[0].usersUpvotes",
                "users[0].usersDownvotes",
                "users[1].comments",
                "users[1].garbageInfo",
                "adminsForm.comments",
                "adminsForm.garbageInfo",
                "map");
    }
}