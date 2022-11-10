package boskicar.com.bcarcontrol;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @See: https://medium.com/@CodyEngel/managing-disposables-in-rxjava-2-for-android-388722ae1e8a
 */
public class DisposableManager {

    private static CompositeDisposable compositeDisposable;

    private DisposableManager() {}

    public static CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        return compositeDisposable;
    }

    public static void add(Disposable disposable) {
        getCompositeDisposable().add(disposable);
    }

    public static void dispose() {
        getCompositeDisposable().dispose();
    }
}
