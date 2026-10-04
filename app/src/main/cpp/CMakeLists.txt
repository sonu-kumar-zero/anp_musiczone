cmake_minimum_required(VERSION 3.22.1)

project("musiczone")

add_library(
        musiczone
        SHARED
        native-lib.cpp
        equalizer/BiquadFilter.cpp
        equalizer/Equalizer.cpp
        equalizer/EqualizerJni.cpp
)

target_link_libraries(
        musiczone
        log
)