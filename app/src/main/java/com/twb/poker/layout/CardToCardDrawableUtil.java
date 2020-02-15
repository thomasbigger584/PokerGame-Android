package com.twb.poker.layout;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.DrawableRes;

import com.twb.poker.domain.Card;

public class CardToCardDrawableUtil {

    @DrawableRes
    public static int getDrawableResFromCard(Context context, Card card) {
        String cardDrawRes = card.getDrawable();
        Resources resources = context.getResources();
        return resources.getIdentifier(cardDrawRes,
                "drawable", context.getPackageName());
    }
}
