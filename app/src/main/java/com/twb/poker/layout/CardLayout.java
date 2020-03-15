package com.twb.poker.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.twb.poker.R;
import com.twb.poker.domain.Card;

public class CardLayout extends FrameLayout {
    private ImageView cardImageView;

    public CardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View inflatedView = inflate(getContext(), R.layout.card, this);
        cardImageView = inflatedView.findViewById(R.id.cardImageView);
        reset();
    }

    public void update(final Card card) {
        post(() -> {
            int cardDrawResId = CardToCardDrawableUtil.getDrawableResFromCard(getContext(), card);
            cardImageView.setImageResource(cardDrawResId);
            cardImageView.setVisibility(VISIBLE);
        });
    }

    public void reset() {
        post(() -> {
            cardImageView.setImageDrawable(null);
            cardImageView.setVisibility(INVISIBLE);
        });
    }
}
