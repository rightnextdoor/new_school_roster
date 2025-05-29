/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/profile/PhotoCropper.tsx

import React, { useState, useCallback } from 'react';
import Cropper from 'react-easy-crop';
import type { Area } from 'react-easy-crop';

interface Props {
  src: string; // DataURL or image URL
  onComplete: (croppedDataUrl: string) => void;
  onCancel: () => void;
}

/**
 * Utility to create a cropped image as a DataURL
 */
async function getCroppedImg(
  imageSrc: string,
  pixelCrop: Area
): Promise<string> {
  const image = await new Promise<HTMLImageElement>((resolve, reject) => {
    const img = new Image();
    img.src = imageSrc;
    img.onload = () => resolve(img);
    img.onerror = (error) => reject(error);
  });

  const canvas = document.createElement('canvas');
  const ctx = canvas.getContext('2d');
  if (!ctx) throw new Error('Failed to get canvas context');

  canvas.width = pixelCrop.width;
  canvas.height = pixelCrop.height;

  ctx.drawImage(
    image,
    pixelCrop.x,
    pixelCrop.y,
    pixelCrop.width,
    pixelCrop.height,
    0,
    0,
    pixelCrop.width,
    pixelCrop.height
  );

  return canvas.toDataURL('image/jpeg');
}

const PhotoCropper: React.FC<Props> = ({ src, onComplete, onCancel }) => {
  const [crop, setCrop] = useState<{ x: number; y: number }>({ x: 0, y: 0 });
  const [zoom, setZoom] = useState(1);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState<Area | null>(null);

  const onCropComplete = useCallback((_: Area, pixels: Area) => {
    setCroppedAreaPixels(pixels);
  }, []);

  const handleApply = useCallback(async () => {
    if (croppedAreaPixels) {
      const croppedDataUrl = await getCroppedImg(src, croppedAreaPixels);
      onComplete(croppedDataUrl);
    }
  }, [croppedAreaPixels, onComplete, src]);

  return (
    <div className="photo-cropper-container">
      <div
        className="cropper-wrapper"
        style={{
          position: 'relative',
          width: 300,
          height: 300,
          borderRadius: '50%',
          overflow: 'hidden',
          margin: '0 auto',
        }}
      >
        <Cropper
          image={src}
          crop={crop}
          zoom={zoom}
          aspect={1}
          cropShape="round"
          showGrid={false}
          onCropChange={setCrop}
          onZoomChange={setZoom}
          onCropComplete={onCropComplete}
        />
      </div>

      {/* Zoom Slider */}
      <div
        className="controls teal-button"
        style={{
          width: 300,
          margin: '16px auto',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          gap: '8px',
        }}
      >
        <span className="text-white font-semibold">Zoom</span>
        <input
          type="range"
          min={1}
          max={3}
          step={0.1}
          value={zoom}
          onChange={(e) => setZoom(Number(e.target.value))}
          className="flex-grow"
        />
      </div>

      {/* Action Buttons: same width as cropper, side by side */}
      <div
        className="actions"
        style={{
          width: 300,
          margin: '24px auto 0',
          display: 'flex',
          justifyContent: 'space-between',
        }}
      >
        <button type="button" onClick={onCancel} className="teal-button">
          Cancel
        </button>
        <button type="button" onClick={handleApply} className="teal-button">
          Apply
        </button>
      </div>
    </div>
  );
};

export default PhotoCropper;
