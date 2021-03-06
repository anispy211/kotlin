/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.k2js.translate.reference;

import com.google.dart.compiler.backend.js.ast.JsExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.psi.*;
import org.jetbrains.k2js.translate.context.TranslationContext;

/**
 * @author Pavel Talanov
 */
public final class AccessTranslationUtils {
    private AccessTranslationUtils() {
    }

    //TODO: this piece of code represents dangerously convoluted logic, think of the ways it can be improved
    @NotNull
    public static AccessTranslator getAccessTranslator(@NotNull JetExpression referenceExpression,
                                                       @NotNull TranslationContext context) {
        assert ((referenceExpression instanceof JetReferenceExpression) ||
                (referenceExpression instanceof JetQualifiedExpression));
        if (PropertyAccessTranslator.canBePropertyAccess(referenceExpression, context)) {
            if (referenceExpression instanceof JetQualifiedExpression) {
                return QualifiedExpressionTranslator.getAccessTranslator((JetQualifiedExpression) referenceExpression, context);
            }
            assert referenceExpression instanceof JetSimpleNameExpression;
            return PropertyAccessTranslator.newInstance((JetSimpleNameExpression) referenceExpression,
                                                        null, CallType.NORMAL, context);
        }
        if (referenceExpression instanceof JetArrayAccessExpression) {
            return ArrayAccessTranslator.newInstance((JetArrayAccessExpression) referenceExpression, context);
        }
        return ReferenceAccessTranslator.newInstance((JetSimpleNameExpression) referenceExpression, context);
    }

    @NotNull
    public static CachedAccessTranslator getCachedAccessTranslator(@NotNull JetExpression referenceExpression,
                                                                   @NotNull TranslationContext context) {
        return getAccessTranslator(referenceExpression, context).getCached();
    }

    @NotNull
    public static JsExpression translateAsGet(@NotNull JetReferenceExpression expression,
                                              @NotNull TranslationContext context) {
        return (getAccessTranslator(expression, context)).translateAsGet();
    }
}
