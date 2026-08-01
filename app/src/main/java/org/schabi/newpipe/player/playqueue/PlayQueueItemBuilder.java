package org.schabi.newpipe.player.playqueue;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.ViewCompat;

import org.schabi.newpipe.util.Localization;
import org.schabi.newpipe.util.ServiceHelper;
import org.schabi.newpipe.util.image.CoilHelper;

public class PlayQueueItemBuilder {
    private static final String TAG = PlayQueueItemBuilder.class.toString();
    private OnSelectedListener onItemClickListener;

    public PlayQueueItemBuilder(final Context context) {
    }

    public void setOnSelectedListener(final OnSelectedListener listener) {
        this.onItemClickListener = listener;
    }

    public void buildStreamInfoItem(final PlayQueueItemHolder holder, final PlayQueueItem item) {
        if (!TextUtils.isEmpty(item.getTitle())) {
            holder.itemVideoTitleView.setText(item.getTitle());
        }
        holder.itemAdditionalDetailsView.setText(Localization.concatenateStrings(item.getUploader(),
                ServiceHelper.getNameOfServiceById(item.getServiceId())));

        String duration = "";
        if (item.getDuration() > 0) {
            duration = Localization.getDurationString(item.getDuration());
            holder.itemDurationView.setText(duration);
            holder.itemDurationView.setVisibility(View.VISIBLE);
        } else {
            holder.itemDurationView.setVisibility(View.GONE);
        }

        CoilHelper.INSTANCE.loadThumbnail(holder.itemThumbnailView, item.getThumbnails());
        ViewCompat.setImportantForAccessibility(holder.itemThumbnailView,
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        ViewCompat.setImportantForAccessibility(holder.itemVideoTitleView,
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        ViewCompat.setImportantForAccessibility(holder.itemAdditionalDetailsView,
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        ViewCompat.setImportantForAccessibility(holder.itemDurationView,
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        ViewCompat.setImportantForAccessibility(holder.itemHandle,
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        holder.itemRoot.setContentDescription(Localization.concatenateStrings(item.getTitle(),
                item.getUploader(), ServiceHelper.getNameOfServiceById(item.getServiceId()),
                duration));

        holder.itemRoot.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.selected(item, view);
            }
        });

        holder.itemRoot.setOnLongClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.held(item, view);
                return true;
            }
            return false;
        });

        holder.itemHandle.setOnTouchListener(getOnTouchListener(holder));
    }

    private View.OnTouchListener getOnTouchListener(final PlayQueueItemHolder holder) {
        return (view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN
                    && onItemClickListener != null) {
                onItemClickListener.onStartDrag(holder);
            }
            return false;
        };
    }

    public interface OnSelectedListener {
        void selected(PlayQueueItem item, View view);

        void held(PlayQueueItem item, View view);

        void onStartDrag(PlayQueueItemHolder viewHolder);
    }
}
