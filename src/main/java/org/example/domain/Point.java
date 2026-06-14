package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {
    private Float x;
    private Float y;
    private Float r;
    private Float temperature;
    private boolean hit;
}
