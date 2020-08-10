package com.iacob.idchanger.utils;

import android.text.InputFilter;
import android.text.Spanned;
import java.util.regex.Pattern;

public class IDInputFilter implements InputFilter {

    private Pattern pattern;

    public IDInputFilter(String str) {
        this(Pattern.compile(str));
    }

    public IDInputFilter(Pattern pattern) {
        if (pattern == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(pattern);
            sb.append(" requires a regex.");
            throw new IllegalArgumentException(sb.toString());
        }
        this.pattern = pattern;
    }

    public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        if (!this.pattern.matcher(charSequence).matches()) {
            return "";
        }
        return null;
    }
}
