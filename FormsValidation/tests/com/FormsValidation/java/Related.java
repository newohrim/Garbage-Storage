package com.FormsValidation.java;

@Constrained
public class Related {
    @Positive
    private int x;

    public Related(int x)
    {
        this.x = x;
    }
}
