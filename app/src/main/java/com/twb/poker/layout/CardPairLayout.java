package com.twb.poker.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.twb.poker.R;
import com.twb.poker.domain.Card;

import java.util.Locale;

public class CardPairLayout extends FrameLayout {
    private ImageView[] cardImageViews = new ImageView[2];
    private TextView displayNameTextView;
    private TextView fundsTextView;

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
        displayNameTextView = inflatedView.findViewById(R.id.displayNameTextView);
        fundsTextView = inflatedView.findViewById(R.id.fundsTextView);
        clear();
    }

    public void clear() {
        for (ImageView imageView : cardImageViews) {
            imageView.setImageResource(R.drawable.back);
            imageView.setVisibility(INVISIBLE);
        }
    }

    public void update(final Card card) {
        post(() -> {
            int cardDrawResId = CardToCardDrawableUtil.getDrawableResFromCard(getContext(), card);

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
        });
    }

    public void update(final Double funds) {
        post(() -> {
            fundsTextView.setText(String.format(Locale.getDefault(), "%.2f", funds));
        });
    }

    public void update(final String displayName) {
        post(() -> {
            displayNameTextView.setText(displayName);
        });
    }
}
