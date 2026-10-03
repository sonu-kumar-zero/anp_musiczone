#pragma once

#include "BiquadFilter.h"

class Equalizer {
public:
    static constexpr int BAND_COUNT = 5;

    Equalizer();

    void configure(double sampleRate);

    void setBandGain(int band, double gainDb);

    void process(float *samples, int frameCount);

    void reset();

private:
    static constexpr double FREQUENCIES[BAND_COUNT] = {
            60.0,
            230.0,
            910.0,
            3600.0,
            14000.0
    };

    static constexpr double DEFAULT_Q = 1.0;

    BiquadFilter left_[BAND_COUNT];
    BiquadFilter right_[BAND_COUNT];

    double sampleRate_ = 48000.0;

    double gains_[BAND_COUNT] = {
            0.0,
            0.0,
            0.0,
            0.0,
            0.0
    };

    void configureBand(int band);
};