#include <jni.h>

#include "equalizer/Equalizer.h"

namespace {

    Equalizer equalizer;

    bool equalizerConfigured = false;

    void ensureEqualizerConfigured() {
        if (equalizerConfigured) {
            return;
        }

        equalizer.configure(48000.0);
        equalizer.setBandGain(0, 12.0);
        equalizerConfigured = true;
    }

}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_example_musiczone_MainActivity_testEqualizer(
        JNIEnv* env,
        jobject /* this */,
        jfloatArray input
) {
    ensureEqualizerConfigured();

    const jsize sampleCount =
            env->GetArrayLength(input);

    if (sampleCount % 2 != 0) {
        return nullptr;
    }

    jfloat* samples =
            env->GetFloatArrayElements(input, nullptr);

    if (samples == nullptr) {
        return nullptr;
    }

    const int frameCount =
            static_cast<int>(sampleCount / 2);

    equalizer.process(
            samples,
            frameCount
    );

    jfloatArray output =
            env->NewFloatArray(sampleCount);

    if (output != nullptr) {
        env->SetFloatArrayRegion(
                output,
                0,
                sampleCount,
                samples
        );
    }

    env->ReleaseFloatArrayElements(
            input,
            samples,
            JNI_ABORT
    );

    return output;
}