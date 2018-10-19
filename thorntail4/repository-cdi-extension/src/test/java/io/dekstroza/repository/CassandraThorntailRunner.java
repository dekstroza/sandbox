package io.dekstroza.repository;

import io.thorntail.test.ThorntailTestRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.List;

public class CassandraThorntailRunner extends ThorntailTestRunner {
  public CassandraThorntailRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  public void run(RunNotifier notifier) {
    beforeClass();
    super.run(notifier);
    afterClass();
  }

  protected void beforeClass() {
    final List<FrameworkMethod> beforeClass =
        this.getTestClass().getAnnotatedMethods(BeforeClass.class);
    final List<FrameworkMethod> afterClass =
        this.getTestClass().getAnnotatedMethods(AfterClass.class);
    if (!beforeClass.isEmpty()) {
      try {
        beforeClass.get(0).getMethod().invoke(this.getTestClass());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected void afterClass() {
    final List<FrameworkMethod> afterClass =
        this.getTestClass().getAnnotatedMethods(AfterClass.class);
    if (!afterClass.isEmpty()) {
      try {
        afterClass.get(0).getMethod().invoke(this.getTestClass());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
