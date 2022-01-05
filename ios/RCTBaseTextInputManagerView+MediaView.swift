//
//  RCTBaseTextInputManagerView+MediaView.swift
//  MediaInput
//
//  Created by Konstantin Späth on 04.01.22.
//  Copyright © 2022 Facebook. All rights reserved.
//

import Foundation
import MobileCoreServices

public extension RCTBaseTextInputView {
    
    struct Holder {
        static var acceptedTypes = [kUTTypeGIF, kUTTypePNG, kUTTypeJPEG] as [String]
    }
    
    func pasteboardHasImages() -> Bool {
        if #available(iOS 10.0, *) {
            return UIPasteboard.general.hasImages
        } else {
            return extractImageFromPasteboard() != nil
        }
    }
    
    func extractImageFromPasteboard() -> Data? {
        let generalPasteboard = UIPasteboard.general
        
        for type in Holder.acceptedTypes {
            let data = generalPasteboard.data(forPasteboardType: type)
            if(data != nil){
                return data
            }
        }
        return nil
    }
    
    override func paste(_ sender: Any?) {
        if self.onImageChange == nil {
            super.paste(sender)
            return
        }
        
        var hasImage = false
        var image : Data?
        
        if #available(iOS 10.0, *) {
            hasImage = extractImageFromPasteboard() != nil
        } else {
            image = extractImageFromPasteboard()
            hasImage = (image != nil)
        }
        
        if hasImage {
            if #available(iOS 10.0, *){
                image = extractImageFromPasteboard()
            }
            DispatchQueue.global().async {
                let mimeType = image?.mimeTypeByGuessingFromData()
                let fileExtension = mimeType?.replacingOccurrences(of: "image/", with: "")
                
                
                let base64 = image?.base64EncodedString(options: Data.Base64EncodingOptions.init(rawValue: 0))
                
                let paths = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)
                
                let uuid = UUID()
                let uniqueFileName = uuid.uuidString
                let path = String(format: "%@/%@.%@", paths[0], uniqueFileName, fileExtension!)
                
                do{
                    try image?.write(to: URL(fileURLWithPath: path))
                } catch {
                    print(error)
                }
                
                NSLog("%@", path)
                
               self.onImageChange?([
                    "data": base64!,
                    "uri": path,
                    "mime": mimeType!,
                                  ])
                
                
                
            }
        } else {
            super.paste(sender)
        }
        
    }
    
    override func canPerformAction(_ action: Selector, withSender sender: Any?) -> Bool {
        if action == #selector(UIResponderStandardEditActions.paste(_:)) && self.pasteboardHasImages() {
            return self.onImageChange != nil
        }
        return super.canPerformAction(action, withSender: sender)
    }
    
}
