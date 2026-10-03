#include "Equalizer.h"

Equalizer::Equalizer() {
    configure(sampleRate_);
}

void Equalizer::configure(double sampleRate) {
    sampleRate_ = sampleRate;

    for (int band = 0; band < BAND_COUNT; ++band) {
        configureBand(band);
    }
}

void Equalizer::configureBand(int band) {
    if (band < 0 || band >= BAND_COUNT) {
        return;
    }

    left_[band].configure(
            sampleRate_,
            FREQUENCIES[band],
            gains_[band],
            DEFAULT_Q
    );

    right_[band].configure(
            sampleRate_,
            FREQUENCIES[band],
            gains_[band],
            DEFAULT_Q
    );
}

void Equalizer::setBandGain(
        int band,
        double gainDb
) {
    if (band < 0 || band >= BAND_COUNT) {
        return;
    }

    gains_[band] = gainDb;

    configureBand(band);
}

void Equalizer::process(
        float* samples,
        int frameCount
) {
    if (samples == nullptr || frameCount <= 0) {
        return;
    }

    for (int frame = 0; frame < frameCount; ++frame) {
        const int index = frame * 2;

        float leftSample = samples[index];
        float rightSample = samples[index + 1];

        for (int band = 0; band < BAND_COUNT; ++band) {
            leftSample = left_[band].process(leftSample);
            rightSample = right_[band].process(rightSample);
        }

        samples[index] = leftSample;
        samples[index + 1] = rightSample;
    }
}

void Equalizer::reset() {
    for (int band = 0; band < BAND_COUNT; ++band) {
        left_[band].reset();
        right_[band].reset();
    }
}