#pragma once

class BiquadFilter {
public:
    void configure(
            double sampleRate,
            double centerFrequency,
            double gainDb,
            double q
    );

    float process(float input);

    void reset();

private:
    double b0_ = 1.0;
    double b1_ = 0.0;
    double b2_ = 0.0;

    double a1_ = 0.0;
    double a2_ = 0.0;

    double x1_ = 0.0;
    double x2_ = 0.0;

    double y1_ = 0.0;
    double y2_ = 0.0;
};