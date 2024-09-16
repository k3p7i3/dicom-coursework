import * as cornerstone from "cornerstone-core";
import * as cornerstoneMath from "cornerstone-math";
import * as cornerstoneTools from "cornerstone-tools";
import Hammer from "hammerjs";
import * as  cornerstoneWebImageLoader from "cornerstone-web-image-loader";


export const initCornerstone = (authToken) => {

  cornerstoneTools.external.cornerstone = cornerstone;
  cornerstoneTools.external.cornerstoneMath = cornerstoneMath;
  cornerstoneTools.external.Hammer = Hammer;

  cornerstoneWebImageLoader.external.cornerstone = cornerstone;
  cornerstoneWebImageLoader.configure({
    beforeSend: function (xhr) {
      xhr.setRequestHeader('Authorization', `Basic ${authToken}`);
  }
  });
}