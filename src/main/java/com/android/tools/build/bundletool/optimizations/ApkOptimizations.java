/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com.android.tools.build.bundletool.optimizations;

import static com.android.tools.build.bundletool.model.OptimizationDimension.ABI;
import static com.android.tools.build.bundletool.model.OptimizationDimension.LANGUAGE;
import static com.android.tools.build.bundletool.model.OptimizationDimension.SCREEN_DENSITY;
import static com.google.common.base.Preconditions.checkNotNull;

import com.android.tools.build.bundletool.model.OptimizationDimension;
import com.android.tools.build.bundletool.version.Version;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;

/** Optimizations that should be performed on the generated APKs. */
@AutoValue
public abstract class ApkOptimizations {

  /**
   * List of optimizations performed on APKs keyed by BundleTool version where they were introduced.
   *
   * <p>When we introduce new optimizations, this allows us to enable them for developers using the
   * latest version of BundleTool and not affecting developers who built the bundle using an older
   * version of BundleTool.
   */
  private static final ImmutableSortedMap<Version, ApkOptimizations>
      DEFAULT_OPTIMIZATIONS_BY_BUNDLETOOL_VERSION =
          ImmutableSortedMap.<Version, ApkOptimizations>naturalOrder()
              .put(
                  Version.of("0.0.0-dev"),
                  ApkOptimizations.builder()
                      .setSplitDimensions(ImmutableSet.of(ABI, SCREEN_DENSITY, LANGUAGE))
                      .build())
              .build();

  public abstract ImmutableSet<OptimizationDimension> getSplitDimensions();

  static Builder builder() {
    return new AutoValue_ApkOptimizations.Builder();
  }

  /** Builder for the {@link ApkOptimizations} class. */
  @AutoValue.Builder
  abstract static class Builder {
    abstract Builder setSplitDimensions(ImmutableSet<OptimizationDimension> splitDimensions);

    abstract ApkOptimizations build();
  }

  /**
   * Returns the default optimizations to perform on the generated APKs given the version of
   * BundleTool used to build the App Bundle.
   *
   * <p>The default optimizations are optimizations performed if the developer hasn't provided any
   * other instructions.
   */
  public static ApkOptimizations getDefaultOptimizationsForVersion(Version bundleToolVersion) {
    // We use the default optimizations of the highest version that is below or equal to the build
    // version.
    return checkNotNull(
            DEFAULT_OPTIMIZATIONS_BY_BUNDLETOOL_VERSION.floorEntry(bundleToolVersion),
            "No default optimizations found for BundleTool version %s.",
            bundleToolVersion)
        .getValue();
  }

  /** Returns an optimizations specific to the universal APK. */
  public static ApkOptimizations getOptimizationsForUniversalApk() {
    // Currently no optimizations are performed.
    return ApkOptimizations.builder().setSplitDimensions(ImmutableSet.of()).build();
  }
}
