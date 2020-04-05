package com.twb.poker.util;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.DrawableRes;

import com.twb.poker.domain.Card;

public class CardToCardDrawableUtil {
    private static final String DEF_TYPE = "drawable";

    @DrawableRes
    public static int getDrawableResFromCard(Context context, Card card) {
        String cardDrawRes = card.getDrawable();
        Resources resources = context.getResources();
        return resources.getIdentifier(cardDrawRes,
                DEF_TYPE, context.getPackageName());
    }
}
