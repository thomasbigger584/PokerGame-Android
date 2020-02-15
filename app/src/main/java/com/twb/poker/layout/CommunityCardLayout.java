package com.twb.poker.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.twb.poker.R;

public class CommunityCardLayout extends FrameLayout {

    private CardLayout community1CardLayout;
    private CardLayout community2CardLayout;
    private CardLayout community3CardLayout;
    private CardLayout community4CardLayout;
    private CardLayout community5CardLayout;

    public CommunityCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public CommunityCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View inflatedView = inflate(getContext(), R.layout.community_cards, this);
        community1CardLayout = inflatedView.findViewById(R.id.community1CardLayout);
        community2CardLayout = inflatedView.findViewById(R.id.community2CardLayout);
        community3CardLayout = inflatedView.findViewById(R.id.community3CardLayout);
        community4CardLayout = inflatedView.findViewById(R.id.community4CardLayout);
        community5CardLayout = inflatedView.findViewById(R.id.community5CardLayout);
    }
}
