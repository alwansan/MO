#include <jni.h>
#include <GLES2/gl2.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_alwansan_mo_glesprobe_ProbeNativeRenderer_nativeRenderer(JNIEnv* env, jclass) {
    const GLubyte* value = glGetString(GL_RENDERER);
    return env->NewStringUTF(value ? reinterpret_cast<const char*>(value) : "(null renderer)");
}
