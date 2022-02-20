
import { NativeSyntheticEvent } from "react-native";
declare module "react-native" {
  type ImageChangeEventData = {
    uri: string;
    data: string;
    linkUri?: string | null;
    mime?: string | null;
  };
  type ImageChangeEvent = NativeSyntheticEvent<ImageChangeEventData>;

  interface TextInputProps {
    onImageChange?: (event: ImageChangeEvent) => void;
  }
}
