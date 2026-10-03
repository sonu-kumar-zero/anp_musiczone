#include "BiquadFilter.h"

#include <cmath>

namespace {

    constexpr double PI = 3.14159265358979323846;

}

void BiquadFilter::configure(
        double sampleRate,
        double centerFrequency,
        double gainDb,
        double q
) {
    const double amplitude = std::pow(10.0, gainDb / 40.0);

    const double omega =
            2.0 * PI * centerFrequency / sampleRate;

    const double sinOmega = std::sin(omega);
    const double cosOmega = std::cos(omega);

    const double alpha =
            sinOmega / (2.0 * q);

    const double rawB0 =
            1.0 + alpha * amplitude;

    const double rawB1 =
            -2.0 * cosOmega;

    const double rawB2 =
            1.0 - alpha * amplitude;

    const double rawA0 =
            1.0 + alpha / amplitude;

    const double rawA1 =
            -2.0 * cosOmega;

    const double rawA2 =
            1.0 - alpha / amplitude;

    b0_ = rawB0 / rawA0;
    b1_ = rawB1 / rawA0;
    b2_ = rawB2 / rawA0;

    a1_ = rawA1 / rawA0;
    a2_ = rawA2 / rawA0;
}

float BiquadFilter::process(float input) {
    const double output =
            b0_ * input +
            b1_ * x1_ +
            b2_ * x2_ -
            a1_ * y1_ -
            a2_ * y2_;

    x2_ = x1_;
    x1_ = input;

    y2_ = y1_;
    y1_ = output;

    return static_cast<float>(output);
}

void BiquadFilter::reset() {
    x1_ = 0.0;
    x2_ = 0.0;

    y1_ = 0.0;
    y2_ = 0.0;
}