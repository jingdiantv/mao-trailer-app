package com.movie.maotrailer.ui.detail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import com.movie.maotrailer.api.NetworkState;
import com.movie.maotrailer.api.repository.EndpointRepository;
import com.movie.maotrailer.data.remote.addendum.Cast;
import com.movie.maotrailer.data.remote.addendum.CastCrewResponse;
import com.movie.maotrailer.data.remote.addendum.Crew;
import com.movie.maotrailer.helper.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class DetailViewModel extends ViewModel {

    private EndpointRepository endpointRepository;
    private CompositeDisposable compositeDisposable;

    private MutableLiveData<CastCrewResponse> mCastCrewResponse;
    private MutableLiveData<NetworkState> mInitialLoading;
    private String mType;
    private int mId;

    @Inject
    DetailViewModel(EndpointRepository endpointRepository, CompositeDisposable compositeDisposable) {
        this.endpointRepository = endpointRepository;
        this.compositeDisposable = compositeDisposable;

        mCastCrewResponse = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();
    }

    public void setPreconditions(String type, int id) {
        this.mType = type;
        this.mId = id;
    }

    public void fetchCastCrew() {
        Disposable castCrew = endpointRepository.getCastCrewList(mType, mId)
                .filter(castCrewResponse -> {
                    filterCrew(castCrewResponse);
                    return true;
                })
                .doOnComplete(() -> Timber.d("Crew filtering completed"))
                .filter(castCrewResponse -> {
                    filterCast(castCrewResponse);
                    return true;
                })
                .doOnComplete(() -> Timber.d("Cast filtering completed"))
                .subscribe(this::onSuccess, this::onError);

        compositeDisposable.add(castCrew);
    }

    private void onError(Throwable throwable) {
        mInitialLoading.setValue(new NetworkState(NetworkState.Status.FAILED));
        Timber.d(throwable);
    }

    private void onSuccess(CastCrewResponse castCrewResponse) {
        if (castCrewResponse != null) {
            mInitialLoading.setValue(NetworkState.LOADED);
            mCastCrewResponse.setValue(castCrewResponse);
        } else {
            mInitialLoading.setValue(new NetworkState(NetworkState.Status.NO_ITEM));
        }
    }

    private void filterCrew(CastCrewResponse castCrewResponse) {
        if (castCrewResponse != null) {
            List<Crew> crewList = new ArrayList<>();

            if (castCrewResponse.getCredits().getCrews().size() > 0) {
                Crew crew;

                for (int i = 0; i < castCrewResponse.getCredits().getCrews().size(); i++) {
                    if (castCrewResponse.getCredits().getCrews().get(i).getJob().equals(Constants.DIRECTOR_PREFIX)) {
                        crew = new Crew();

                        crew.setJob(castCrewResponse.getCredits().getCrews().get(i).getJob());
                        crew.setName(castCrewResponse.getCredits().getCrews().get(i).getName());
                        crew.setProfilePath(castCrewResponse.getCredits().getCrews().get(i).getProfilePath());

                        crewList.add(i, crew);
                    }
                }
            } else {
                crewList.add(null);
            }

            castCrewResponse.getCredits().setCrews(crewList);
        }
    }

    private void filterCast(CastCrewResponse castCrewResponse) {
        if (castCrewResponse != null) {
            List<Cast> castList = new ArrayList<>();

            if (castCrewResponse.getCredits().getCasts().size() > 0) {
                Cast cast;

                for (int j = 0; j < castCrewResponse.getCredits().getCasts().size(); j++) {
                    cast = new Cast();
                    if (!TextUtils.isEmpty(castCrewResponse.getCredits().getCasts().get(j).getProfilePath())) {
                        cast.setProfilePath(castCrewResponse.getCredits().getCasts().get(j).getProfilePath());
                        cast.setName(castCrewResponse.getCredits().getCasts().get(j).getName());

                        castList.add(j, cast);
                    } else {
                        cast.setProfilePath("null");
                        cast.setName(castCrewResponse.getCredits().getCasts().get(j).getName());

                        castList.add(j, cast);
                    }
                }
            } else {
                castList.add(null);
            }

            castCrewResponse.getCredits().setCasts(castList);
        }
    }

    public MutableLiveData<CastCrewResponse> getCastCrewResponseMutableLiveData() {
        return mCastCrewResponse;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}
