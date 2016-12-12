/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.appservice.samples;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.util.ArrayList;
import java.util.List;

public class StorageConnector {
    private static String storageConnectionString = System.getenv("storage.connectionString");
    private static String storageContainerName = System.getenv("storage.containerName");

    public static String traverseBlobs() throws Exception {
        if (storageConnectionString == null) {
            storageConnectionString = System.getenv("CUSTOMCONNSTR_storage.connectionString");
        }
        // Setup the cloud storage account.
        CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
        // Create a blob service client
        CloudBlobClient blobClient = account.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference(storageContainerName);
        container.createIfNotExists();
        BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
        // Include public access in the permissions object
        containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
        // Set the permissions on the container
        container.uploadPermissions(containerPermissions);

        List<String> blobs = new ArrayList<>(listBlobs(container));
        for(CloudBlobDirectory dir : listDirectories(container)) {
            blobs.addAll(listBlobs(dir));
        }
        return "<ul><li>" + String.join("</li><li>", blobs) +
                "</li></ul>";
    }

    private static List<CloudBlobDirectory> listDirectories(CloudBlobContainer container) {
        List<CloudBlobDirectory> directories = new ArrayList<>();
        Iterable<ListBlobItem> items = container.listBlobs();
        for(ListBlobItem item : items) {
            if (item instanceof CloudBlobDirectory) {
                directories.add((CloudBlobDirectory) item);
            }
        }
        return directories;
    }

    private static List<CloudBlobDirectory> listDirectories(CloudBlobDirectory directory) throws Exception {
        List<CloudBlobDirectory> directories = new ArrayList<>();
        Iterable<ListBlobItem> items = directory.listBlobs();
        for(ListBlobItem item : items) {
            if (item instanceof CloudBlobDirectory) {
                directories.add((CloudBlobDirectory) item);
            }
        }
        return directories;
    }

    private static List<String> listBlobs(CloudBlobContainer container) {
        List<ListBlobItem> blobs = new ArrayList<>();
        Iterable<ListBlobItem> items = container.listBlobs();
        for(ListBlobItem item : items) {
            if (! (item instanceof CloudBlobDirectory)) {
                blobs.add(item);
            }
        }
        return Lists.transform(blobs, new Function<ListBlobItem, String>() {
            @Override
            public String apply(ListBlobItem input) {
                String[] segs = input.getUri().getPath().split("/");
                return "<a href='" + input.getUri().toString() + "'>" + segs[segs.length - 1] + "</a>";
            }
        });
    }

    private static List<String> listBlobs(CloudBlobDirectory directory) throws Exception {
        List<ListBlobItem> blobs = new ArrayList<>();
        Iterable<ListBlobItem> items = directory.listBlobs();
        for(ListBlobItem item : items) {
            if (! (item instanceof CloudBlobDirectory)) {
                blobs.add(item);
            }
        }
        List<String> strings = Lists.transform(blobs, new Function<ListBlobItem, String>() {
            @Override
            public String apply(ListBlobItem input) {
                String[] segs = input.getUri().getPath().split("/");
                return "<a href='" + input.getUri().toString() + "'>" + segs[segs.length - 1] + "</a>";
            }
        });
        for(CloudBlobDirectory dir : listDirectories(directory)) {
            strings.addAll(listBlobs(dir));
        }
        return strings;
    }
}
