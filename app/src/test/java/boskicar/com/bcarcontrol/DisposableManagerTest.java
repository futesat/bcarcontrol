package boskicar.com.bcarcontrol;

import org.junit.Before;
import org.junit.Test;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import static org.junit.Assert.*;

public class DisposableManagerTest {

    @Before
    public void setUp() {
        // Ensure we start with a clean state or at least known state if possible
        // But DisposableManager uses a static instance, so be careful.
        // Ideally we should dispose it first.
        DisposableManager.dispose();
    }

    @Test
    public void testGetCompositeDisposable() {
        CompositeDisposable cd = DisposableManager.getCompositeDisposable();
        assertNotNull(cd);
        assertFalse(cd.isDisposed());
    }

    @Test
    public void testAdd() {
        Disposable d = Disposables.empty();
        DisposableManager.add(d);

        CompositeDisposable cd = DisposableManager.getCompositeDisposable();
        assertEquals(1, cd.size());
    }

    @Test
    public void testDispose() {
        Disposable d = Disposables.empty();
        DisposableManager.add(d);

        DisposableManager.dispose();

        CompositeDisposable cd = DisposableManager.getCompositeDisposable();
        // After dispose, the internal reference might be disposed, but the getter
        // creates a new one if disposed?
        // Let's check the implementation:
        // if (compositeDisposable == null || compositeDisposable.isDisposed())
        // compositeDisposable = new CompositeDisposable();
        // So getCompositeDisposable() returns a NEW one if the old one was disposed.
        // So we can't check if the old one is disposed via getter.
        // But we can check that the new one is empty.

        assertEquals(0, cd.size());
        assertFalse(cd.isDisposed());
    }
}
