package boskicar.com.bcarcontrol;

import androidx.annotation.CallSuper;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @See: https://medium.com/@CodyEngel/managing-disposables-in-rxjava-2-for-android-388722ae1e8a
 */
public class DisposingObserver<T> implements Observer<T> {
    @Override
    @CallSuper
    public void onSubscribe(Disposable d) {
        DisposableManager.add(d);
    }

    @Override
    public void onNext(T next) {
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onComplete() {
    }
}
