package com.example.dayout_organizer.adapter.recyclers.myTrips;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.dayout_organizer.R;

import com.example.dayout_organizer.helpers.view.FN;
import com.example.dayout_organizer.models.trip.TripData;
import com.example.dayout_organizer.models.trip.photo.TripPhotoData;
import com.example.dayout_organizer.ui.activities.MainActivity;
import com.example.dayout_organizer.ui.dialogs.notify.DeleteTripOrPollDialog;
import com.example.dayout_organizer.ui.fragments.trips.myTrip.FilterFragment;
import com.example.dayout_organizer.ui.fragments.trips.details.TripDetailsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.dayout_organizer.config.AppConstants.MAIN_FRC;
import static com.example.dayout_organizer.helpers.view.ImageViewer.IMAGE_BASE_URL;

public class ActiveTripAdapter extends RecyclerView.Adapter<ActiveTripAdapter.ViewHolder> {

    List<TripData> list;
    Context context;

    public ActiveTripAdapter(List<TripData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void refresh(List<TripData> list) {
        this.list = list;
        notifyDataSetChanged();
    }



    public void insertRoomObject(TripData tripData) {

        // insert object in room database
        ((MainActivity) context).iTrip
                .insertTripData(tripData)
                .subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    @NonNull
    @Override
    public ActiveTripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_upcoming_trip_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveTripAdapter.ViewHolder holder, int position) {

        insertRoomObject(list.get(position));

        String tripStops = "";
        holder.title.setText(list.get(position).title);
        holder.description.setText(list.get(position).description);
        holder.date.setText(list.get(position).begin_date);
        holder.bindImageSlider(list.get(position).trip_photos);
        holder.deleteIcon.setVisibility(View.GONE);
        holder.activeTV.setVisibility(View.VISIBLE);

        for (int i = 0; i < list.get(position).place_trips.size(); i++) {
            if (i != 0) {
                tripStops += ", " + list.get(position).place_trips.get(i).place.name;
            } else tripStops += list.get(position).place_trips.get(i).place.name;
            ;
        }

        holder.stops = tripStops;
        holder.tripStops.setText(tripStops);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NonConstantResourceId")
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.upcoming_trip_title)
        TextView title;

        @BindView(R.id.upcoming_trip_delete_icon)
        ImageButton deleteIcon;

        @BindView(R.id.upcoming_trip_date)
        TextView date;

        @BindView(R.id.upcoming_trip_description)
        TextView description;

        @BindView(R.id.upcoming_trip_image_slider)
        ImageSlider imageSlider;

        @BindView(R.id.upcoming_trip_active_tv)
        TextView activeTV;

        @BindView(R.id.upcoming_trip_stops)
        TextView tripStops;

        String stops;
        DeleteTripOrPollDialog deleteTripOrPollDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
            init();
        }


        private void init() {
            deleteTripOrPollDialog = new DeleteTripOrPollDialog(context,context.getResources().getString(R.string.deleting_trip));
            deleteTripOrPollDialog.setOnDismissListener(dialog -> notifyDataSetChanged());
            deleteIcon.setOnClickListener(onDeleteClicked);
            activeTV.setVisibility(View.GONE);
        }

        private final View.OnClickListener onDeleteClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTripOrPollDialog.show();
            }
        };

        @Override
        public void onClick(View v) {
            if (!FilterFragment.isFilterOpen) {
                TripData data = list.get(getAdapterPosition());
                data.stopsToDetails = stops;
                FN.addFixedNameFadeFragment(MAIN_FRC, (MainActivity) context, new TripDetailsFragment(data.id,1));
            }
        }

        private void bindImageSlider(List<TripPhotoData> photos) {
            List<SlideModel> slideModels = new ArrayList<>();

            for (TripPhotoData ph : photos) {
                slideModels.add(new SlideModel(IMAGE_BASE_URL+ph.path
                        , ScaleTypes.FIT));
            }

            imageSlider.setImageList(slideModels);

            imageSlider.setScrollBarFadeDuration(10000);
        }

    }
}
