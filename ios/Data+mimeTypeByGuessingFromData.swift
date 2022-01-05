//
//  NSData+mimeTypeByGuessingFromData.swift
//  ImageKeyboard
//
//  Created by Konstantin Späth on 03.01.22.
//  Copyright © 2022 Facebook. All rights reserved.
//

import Foundation

extension Data {

    func mimeTypeByGuessingFromData() -> String! {

        var bytes = [UInt8](repeating: 0, count: 12)
        self.copyBytes(to: &bytes, count: 12)

        let bmp : [UInt8] = [0x42, 0x4D]
        let gif : [UInt8] = [0x47, 0x49, 0x46]
        let jpg : [UInt8] = [0xff, 0xd8, 0xff]
        let psd : [UInt8] = [0x38, 0x42, 0x50, 0x53]
        let iff:[UInt8] = [0x46, 0x4f, 0x52, 0x4d]
        let webp:[UInt8] = [0x52, 0x49, 0x46, 0x46]
        let ico:[UInt8] = [0x00, 0x00, 0x01, 0x00]
        let tif_ii:[UInt8] = [0x49, 0x49, 0x2A, 0x00]
        let tif_mm:[UInt8] = [0x4d, 0x4d, 0x00, 0x2A]
        let png:[UInt8] = [0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]
        let jp2:[UInt8] = [0x00, 0x00, 0x00, 0x0c, 0x6a, 0x50, 0x20, 0x20, 0x0d, 0x0a, 0x87, 0x0a]


        if (memcmp(bytes, bmp, 2) == 0) {
            return "image/x-ms-bmp"
        } else if (memcmp(bytes, gif, 3) == 0) {
            return "image/gif"
        } else if (memcmp(bytes, jpg, 3) == 0) {
            return "image/jpeg"
        } else if (memcmp(bytes, psd, 4) == 0) {
            return "image/psd"
        } else if (memcmp(bytes, iff, 4) == 0) {
            return "image/iff"
        } else if (memcmp(bytes, webp, 4) == 0) {
            return "image/webp"
        } else if (memcmp(bytes, ico, 4) == 0) {
            return "image/vnd.microsoft.icon"
        } else if (memcmp(bytes, tif_ii, 4) == 0) || (memcmp(bytes, tif_mm, 4) == 0) {
            return "image/tiff"
        } else if (memcmp(bytes, png, 8) == 0) {
            return "image/png"
        } else if (memcmp(bytes, jp2, 12) == 0) {
            return "image/jp2"
        }

        return "application/octet-stream" // default type

    }
}

