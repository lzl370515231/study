project(resampler)

add_executable(resampler speex_resampler.c main.c)
target_link_libraries(resampler -lm)