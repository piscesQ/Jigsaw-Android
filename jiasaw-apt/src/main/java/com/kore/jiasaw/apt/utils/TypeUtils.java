package com.kore.jiasaw.apt.utils;

import com.kore.jigsaw.anno.utils.TypeKind;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.kore.jigsaw.anno.utils.Constants.BOOLEAN;
import static com.kore.jigsaw.anno.utils.Constants.BYTE;
import static com.kore.jigsaw.anno.utils.Constants.CHAR;
import static com.kore.jigsaw.anno.utils.Constants.DOUBEL;
import static com.kore.jigsaw.anno.utils.Constants.FLOAT;
import static com.kore.jigsaw.anno.utils.Constants.INTEGER;
import static com.kore.jigsaw.anno.utils.Constants.LONG;
import static com.kore.jigsaw.anno.utils.Constants.PARCELABLE;
import static com.kore.jigsaw.anno.utils.Constants.SERIALIZABLE;
import static com.kore.jigsaw.anno.utils.Constants.SHORT;
import static com.kore.jigsaw.anno.utils.Constants.STRING;

/**
 * @author koreq
 * @date 2021-04-13
 * @description
 */
public class TypeUtils {
    private Types types;
    private TypeMirror parcelableType;
    private TypeMirror serializableType;

    public TypeUtils(Types types, Elements elements) {
        this.types = types;

        parcelableType = elements.getTypeElement(PARCELABLE).asType();
        serializableType = elements.getTypeElement(SERIALIZABLE).asType();
    }

    /**
     * Diagnostics out the true java type
     *
     * @param element Raw type
     * @return Type class of java
     */
    public int typeExchange(Element element) {
        TypeMirror typeMirror = element.asType();

        // Primitive
        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().ordinal();
        }

        switch (typeMirror.toString()) {
            case BYTE:
                return com.kore.jigsaw.anno.utils.TypeKind.BYTE.ordinal();
            case SHORT:
                return com.kore.jigsaw.anno.utils.TypeKind.SHORT.ordinal();
            case INTEGER:
                return com.kore.jigsaw.anno.utils.TypeKind.INT.ordinal();
            case LONG:
                return com.kore.jigsaw.anno.utils.TypeKind.LONG.ordinal();
            case FLOAT:
                return com.kore.jigsaw.anno.utils.TypeKind.FLOAT.ordinal();
            case DOUBEL:
                return com.kore.jigsaw.anno.utils.TypeKind.DOUBLE.ordinal();
            case BOOLEAN:
                return com.kore.jigsaw.anno.utils.TypeKind.BOOLEAN.ordinal();
            case CHAR:
                return com.kore.jigsaw.anno.utils.TypeKind.CHAR.ordinal();
            case STRING:
                return com.kore.jigsaw.anno.utils.TypeKind.STRING.ordinal();
            default:
                // Other side, maybe the PARCELABLE or SERIALIZABLE or OBJECT.
                if (types.isSubtype(typeMirror, parcelableType)) {
                    // PARCELABLE
                    return com.kore.jigsaw.anno.utils.TypeKind.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    // SERIALIZABLE
                    return com.kore.jigsaw.anno.utils.TypeKind.SERIALIZABLE.ordinal();
                } else {
                    return TypeKind.OBJECT.ordinal();
                }
        }
    }
}
