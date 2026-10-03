cmake_minimum_required(VERSION 3.22.1)

project("musiczone")

add_library(
        musiczone
        SHARED
        native-lib.cpp
        equalizer/BiquadFilter.cpp
        equalizer/equalizer.cpp
        equalizer/EqualizerJni.cpp
)