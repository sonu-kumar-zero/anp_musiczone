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

    targetGains_[band].store(
            gainDb,
            std::memory_order_relaxed
    );
}

void Equalizer::process(
        float *samples,
        int frameCount
) {
    if (samples == nullptr || frameCount <= 0) {
        return;
    }

    for (int band = 0; band < BAND_COUNT; ++band) {
        const double targetGain =
                targetGains_[band].load(
                        std::memory_order_relaxed
                );

        const double difference =
                targetGain - gains_[band];

        if (difference != 0.0) {
            gains_[band] +=
                    difference * GAIN_SMOOTHING;

            configureBand(band);
        }
    }

    for (int frame = 0; frame < frameCount; ++frame) {
        const int index = frame * 2;

        float leftSample = samples[index];
        float rightSample = samples[index + 1];

        for (int band = 0; band < BAND_COUNT; ++band) {
            leftSample =
                    left_[band].process(leftSample);

            rightSample =
                    right_[band].process(rightSample);
        }

        samples[index] = leftSample;
        samples[index + 1] = rightSample;
    }
}

void Equalizer::reset() {
    for (int band = 0; band < BAND_COUNT; ++band) {
        gains_[band] = 0.0;
        targetGains_[band].store(
                0.0,
                std::memory_order_relaxed
        );

        left_[band].reset();
        right_[band].reset();

        configureBand(band);
    }
}