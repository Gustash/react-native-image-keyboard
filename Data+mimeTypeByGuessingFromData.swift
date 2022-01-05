//
//  NSData+mimeTypeByGuessingFromData.swift
//  ImageKeyboard
//
//  Created by Konstantin Späth on 03.01.22.
//

import Foundation

extension Data {

    func mimeTypeByGuessingFromData() -> String! {

        var bytes = [UInt8](repeating: 0, count: 12)
        self.copyBytes(to: &bytes, count: 12)

        let bmp : [Character] = ["B", "M"]
        let gif : [Character] = ["G", "I", "F"]
        let jpg : [Character] = [0xff, 0xd8, 0xff].map { Character(UnicodeScalar($0)) }
        let psd : [Character] = ["8", "B", "P", "S"]
        let iff:[Character] = ["F", "O", "R", "M"]
        let webp:[Character] = ["R", "I", "F", "F"]
        let ico:[Character] = [0x00, 0x00, 0x01, 0x00].map { Character(UnicodeScalar($0)) }
        let tif_ii:[Character] = [Unicode.Scalar("I").value,Unicode.Scalar("I").value, 0x2A, 0x00].map { Character(UnicodeScalar($0)!) }
        let tif_mm:[Character] = [Unicode.Scalar("M").value,Unicode.Scalar("M").value, 0x00, 0x2A].map { Character(UnicodeScalar($0)!) }
        let png:[Character] = [0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a].map { Character(UnicodeScalar($0)) }
        let jp2:[Character] = [0x00, 0x00, 0x00, 0x0c, 0x6a, 0x50, 0x20, 0x20, 0x0d, 0x0a, 0x87, 0x0a].map { Character(UnicodeScalar($0)) }


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

