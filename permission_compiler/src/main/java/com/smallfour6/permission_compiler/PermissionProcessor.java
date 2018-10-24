package com.smallfour6.permission_compiler;

import com.google.auto.service.AutoService;
import com.smallfour6.permission_annotation.PermissionDenied;
import com.smallfour6.permission_annotation.PermissionGranted;
import com.smallfour6.permission_annotation.PermissionRationale;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author zhaoxiaosi
 * @desc
 * @create 2018/10/22 下午3:42
 **/
@AutoService(Processor.class)
public class PermissionProcessor extends AbstractProcessor {

    private Elements mElements;
    private Filer mFiler;
    private Map<String, ProxyInfo> mProxyInfoMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElements = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        mProxyInfoMap.clear();
        if (!processAnnotation(roundEnvironment, PermissionGranted.class)) {
            return false;
        }

        if (!processAnnotation(roundEnvironment, PermissionDenied.class)) {
            return false;
        }

        if (!processAnnotation(roundEnvironment, PermissionRationale.class)) {
            return false;
        }
        for (String className : mProxyInfoMap.keySet()) {
            mProxyInfoMap.get(className).generateJavaFile(mFiler);
        }
        return true;
    }

    private boolean processAnnotation(RoundEnvironment roundEnvironment, Class<? extends Annotation> clazz) {
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(clazz)) {
            if (!isValidAnnotation(annotatedElement)) {
                return false;
            }

            TypeElement typeElement = (TypeElement) annotatedElement.getEnclosingElement();
            ExecutableElement executableElement = (ExecutableElement) annotatedElement;

            ProxyInfo proxyInfo = mProxyInfoMap.get(typeElement.getQualifiedName().toString());
            if (proxyInfo == null) {
                proxyInfo = new ProxyInfo(mElements, typeElement);
                mProxyInfoMap.put(typeElement.getQualifiedName().toString(), proxyInfo);
            }

            Annotation annotation = executableElement.getAnnotation(clazz);
            if (annotation instanceof PermissionGranted) {
                int requestCode = ((PermissionGranted) annotation).value();
                proxyInfo.grantedMethodMap.put(requestCode, executableElement.getSimpleName().toString());
            } else if (annotation instanceof PermissionDenied) {
                int requestCode = ((PermissionDenied) annotation).value();
                proxyInfo.deniedMethodMap.put(requestCode, executableElement.getSimpleName().toString());
            } else if (annotation instanceof PermissionRationale) {
                int requestCode = ((PermissionRationale) annotation).value();
                proxyInfo.rationalMethodMap.put(requestCode, executableElement.getSimpleName().toString());
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> types = new HashSet<>();
        types.add(PermissionDenied.class.getCanonicalName());
        types.add(PermissionGranted.class.getCanonicalName());
        types.add(PermissionRationale.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private boolean isValidAnnotation(Element annotatedElement) {
        if (annotatedElement.getKind() != ElementKind.METHOD) {
            return false;
        }

        if (isPrivate(annotatedElement) || isAbstract(annotatedElement)) {
            return false;
        }

        return true;
    }


    private boolean isPrivate(Element annotatedElement) {
        return annotatedElement.getModifiers().contains(Modifier.PRIVATE);
    }

    private boolean isAbstract(Element annotatedElement) {
        return annotatedElement.getModifiers().contains(Modifier.ABSTRACT);
    }
}
