cmake_minimum_required(VERSION 3.22.1)
project(mo_gles_test_layer LANGUAGES CXX)
add_library(MOGlesProofLayer SHARED mo_gles_layer.cpp)
target_compile_features(MOGlesProofLayer PRIVATE cxx_std_17)
target_link_libraries(MOGlesProofLayer log)

# Probe's own NDK GLES reader: compare Java GLES20.glGetString with native libGLESv2
# without loading the proof layer by hand.
find_package(bytehook REQUIRED CONFIG)
add_library(MOProbeRenderer SHARED mo_probe_renderer.cpp mo_dispatch_evidence.cpp mo_p29_gles_hook.cpp)
target_compile_features(MOProbeRenderer PRIVATE cxx_std_17)
target_link_libraries(MOProbeRenderer GLESv2 EGL vulkan dl bytehook::bytehook)
