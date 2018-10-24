package com.smallfour6.permission_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author zhaoxiaosi
 * @desc
 * @create 2018/10/23 上午9:53
 **/
public class ProxyInfo {

    private static final String LINK = "$$";

    private String packageName;
    private String className;
    private TypeElement mTypeElement;

    Map<Integer, String> grantedMethodMap = new HashMap<>();
    Map<Integer, String> deniedMethodMap = new HashMap<>();
    Map<Integer, String> rationalMethodMap = new HashMap<>();


    public ProxyInfo(Elements elements, TypeElement mTypeElement) {
        this.mTypeElement = mTypeElement;
        PackageElement packageElement = elements.getPackageOf(mTypeElement);
        packageName = packageElement.getQualifiedName().toString();
        className = mTypeElement.getSimpleName().toString();
    }


    public void generateJavaFile(Filer filer) {
        ClassName s = ClassName.get("com.smallfour6.permission_lib", "IPermissionProxy");
        TypeName typeName = TypeName.get(mTypeElement.asType());
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(s, typeName);
        TypeSpec typeSpec = TypeSpec.classBuilder(className + LINK + "Permission")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(parameterizedTypeName)
                .addMethod(generateGranted())
                .addMethod(generateDenied())
                .addMethod(generateRationale())
                .addMethod(generateNeedExecuteRationale())
                .build();
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private MethodSpec generateGranted() {
        TypeName t = TypeName.get(mTypeElement.asType());
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .beginControlFlow("switch (requestCode)");

        for (int requestCode : grantedMethodMap.keySet()) {
            codeBlockBuilder.add("case $L:\n", requestCode).indent().addStatement("activity.$L()", grantedMethodMap.get(requestCode)).addStatement("break").unindent();
        }

        codeBlockBuilder.endControlFlow();

        MethodSpec granted = MethodSpec.methodBuilder("granted")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addParameter(t, "activity")
                .addParameter(int.class, "requestCode")
                .addCode(codeBlockBuilder.build())
                .build();


        return granted;
    }

    private MethodSpec generateDenied() {
        TypeName t = TypeName.get(mTypeElement.asType());
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .beginControlFlow("switch (requestCode)");

        for (int requestCode : deniedMethodMap.keySet()) {
            codeBlockBuilder.add("case $L:\n", requestCode).indent().addStatement("activity.$L()", deniedMethodMap.get(requestCode)).addStatement("break").unindent();
        }

        codeBlockBuilder.endControlFlow();

        return MethodSpec.methodBuilder("denied")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addParameter(t, "activity")
                .addParameter(int.class, "requestCode")
                .addCode(codeBlockBuilder.build())
                .build();
    }

    private MethodSpec generateRationale() {
        TypeName t = TypeName.get(mTypeElement.asType());
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .beginControlFlow("switch (requestCode)");

        for (int requestCode : rationalMethodMap.keySet()) {
            codeBlockBuilder.add("case $L:\n", requestCode).indent().addStatement("activity.$L()", rationalMethodMap.get(requestCode)).addStatement("break").unindent();
        }

        codeBlockBuilder.endControlFlow();

        return MethodSpec.methodBuilder("rationale")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addParameter(t, "activity")
                .addParameter(int.class, "requestCode")
                .addCode(codeBlockBuilder.build())
                .build();
    }

    private MethodSpec generateNeedExecuteRationale() {

        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .beginControlFlow("switch (requestCode)");
        for (int requestCode : rationalMethodMap.keySet()) {
            codeBlockBuilder.add("case $L:\n", requestCode).indent().addStatement("return true").unindent();
        }

        codeBlockBuilder.endControlFlow();
        codeBlockBuilder.addStatement("return false");

        return MethodSpec.methodBuilder("needExecuteRationale")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(boolean.class)
                .addParameter(int.class, "requestCode")
                .addCode(codeBlockBuilder.build())
                .build();
    }
}
