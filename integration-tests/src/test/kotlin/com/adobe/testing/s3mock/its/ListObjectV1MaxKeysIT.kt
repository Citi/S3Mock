/*
 *  Copyright 2017-2024 Adobe.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.testing.s3mock.its

import com.amazonaws.services.s3.model.ListObjectsRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

internal class ListObjectV1MaxKeysIT : S3TestBase() {

  @Test
  @S3VerifiedSuccess(year = 2022)
  fun returnsLimitedAmountOfObjectsBasedOnMaxKeys(testInfo: TestInfo) {
    val bucketName = givenBucketWithTwoObjects(testInfo)
    val request = ListObjectsRequest().withBucketName(bucketName).withMaxKeys(1)
    val objectListing = s3Client.listObjects(request)
    assertThat(objectListing.objectSummaries).hasSize(1)
    assertThat(objectListing.maxKeys).isEqualTo(1)
  }

  @Test
  @S3VerifiedSuccess(year = 2022)
  fun returnsAllObjectsIfMaxKeysIsDefault(testInfo: TestInfo) {
    val bucketName = givenBucketWithTwoObjects(testInfo)
    val request = ListObjectsRequest().withBucketName(bucketName)
    val objectListing = s3Client.listObjects(request)
    assertThat(objectListing.objectSummaries).hasSize(2)
    assertThat(objectListing.maxKeys).isEqualTo(1000)
  }

  @Test
  @S3VerifiedSuccess(year = 2022)
  fun returnsAllObjectsIfMaxKeysEqualToAmountOfObjects(testInfo: TestInfo) {
    val bucketName = givenBucketWithTwoObjects(testInfo)
    val request = ListObjectsRequest().withBucketName(bucketName).withMaxKeys(2)
    val objectListing = s3Client.listObjects(request)
    assertThat(objectListing.objectSummaries).hasSize(2)
    assertThat(objectListing.maxKeys).isEqualTo(2)
  }

  @Test
  @S3VerifiedSuccess(year = 2022)
  fun returnsAllObjectsIfMaxKeysMoreThanAmountOfObjects(testInfo: TestInfo) {
    val bucketName = givenBucketWithTwoObjects(testInfo)
    val request = ListObjectsRequest().withBucketName(bucketName).withMaxKeys(3)
    val objectListing = s3Client.listObjects(request)
    assertThat(objectListing.objectSummaries).hasSize(2)
    assertThat(objectListing.maxKeys).isEqualTo(3)
  }

  @Test
  @S3VerifiedSuccess(year = 2022)
  fun returnsEmptyListIfMaxKeysIsZero(testInfo: TestInfo) {
    val bucketName = givenBucketWithTwoObjects(testInfo)
    val request = ListObjectsRequest().withBucketName(bucketName).withMaxKeys(0)
    val objectListing = s3Client.listObjects(request)
    assertThat(objectListing.objectSummaries).hasSize(0)
    assertThat(objectListing.maxKeys).isEqualTo(0)
  }

  @Test
  @S3VerifiedSuccess(year = 2022)
  fun returnsAllObjectsIfMaxKeysIsNegative(testInfo: TestInfo) {
    val bucketName = givenBucketWithTwoObjects(testInfo)
    val request = ListObjectsRequest().withBucketName(bucketName).withMaxKeys(-1)
    val objectListing = s3Client.listObjects(request)

    // Apparently, the Amazon SDK rejects negative max keys, and by default it's 1000
    assertThat(objectListing.objectSummaries).hasSize(2)
    assertThat(objectListing.maxKeys).isEqualTo(1000)
  }

  private fun givenBucketWithTwoObjects(testInfo: TestInfo): String {
    val bucketName = givenBucketV1(testInfo)
    s3Client.putObject(bucketName, "a", "")
    s3Client.putObject(bucketName, "b", "")
    return bucketName
  }
}
