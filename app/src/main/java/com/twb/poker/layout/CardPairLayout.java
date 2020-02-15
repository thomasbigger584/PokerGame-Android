package com.twb.poker.layout;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.twb.poker.R;
import com.twb.poker.domain.Card;

public class CardPairLayout extends FrameLayout {

    private ImageView[] cardImageViews = new ImageView[2];

    public CardPairLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public CardPairLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View inflatedView = inflate(getContext(), R.layout.card_pair, this);
        cardImageViews[0] = inflatedView.findViewById(R.id.leftCardImageView);
        cardImageViews[1] = inflatedView.findViewById(R.id.rightCardImageView);
        clear();
    }

    public void clear() {
        for (ImageView imageView : cardImageViews) {
            imageView.setImageResource(R.drawable.back);
            imageView.setVisibility(INVISIBLE);
        }
    }

    public void update(final Card card) {
        post(new Runnable() {
            @Override
            public void run() {
                int cardDrawResId = getDrawableResFromCard(card);

                if (cardImageViews[0].getVisibility() != INVISIBLE &&
                        cardImageViews[1].getVisibility() != INVISIBLE) {
                    clear();
                }
                if (cardImageViews[0].getVisibility() == INVISIBLE) {
                    cardImageViews[0].setImageResource(cardDrawResId);
                    cardImageViews[0].setVisibility(VISIBLE);
                } else if (cardImageViews[1].getVisibility() == INVISIBLE) {
                    cardImageViews[1].setImageResource(cardDrawResId);
                    cardImageViews[1].setVisibility(VISIBLE);
                }
            }
        });
    }

    @DrawableRes
    private int getDrawableResFromCard(Card card) {
        String cardDrawRes = card.getDrawable();
        Context context = getContext();
        Resources resources = getResources();

        return resources.getIdentifier(cardDrawRes,
                "drawable", context.getPackageName());
    }
}
