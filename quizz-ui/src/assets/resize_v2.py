import cv2
import numpy as np

# Load the image with alpha channel (transparency)
image = cv2.imread('./images/logo.png', cv2.IMREAD_UNCHANGED)

# Define the target width and height (container size)
target_width = 42
target_height = 42

def resize_contain(image, target_width, target_height):
    # Check if the image has an alpha channel (transparency)
    if image.shape[2] == 4:  # BGRA (4 channels)
        # Split into color and alpha channels
        bgr = image[:, :, :3]  # Color channels (BGR)
        alpha = image[:, :, 3]  # Alpha channel (transparency)
    else:
        # If no alpha, create a fully opaque alpha channel
        bgr = image
        alpha = np.ones((image.shape[0], image.shape[1]), dtype=np.uint8) * 255

    # Get original dimensions
    h, w = bgr.shape[:2]
    original_aspect = w / h
    target_aspect = target_width / target_height

    # Calculate scaling factor based on target aspect ratio and image aspect ratio
    if original_aspect > target_aspect:
        scale = target_width / w  # Scale by width
    else:
        scale = target_height / h  # Scale by height

    # Calculate new dimensions keeping the aspect ratio intact
    new_w = int(w * scale)
    new_h = int(h * scale)

    # Resize color and alpha channels separately to maintain transparency
    foreground_bgr = cv2.resize(bgr, (new_w, new_h), interpolation=cv2.INTER_AREA)
    foreground_alpha = cv2.resize(alpha, (new_w, new_h), interpolation=cv2.INTER_AREA)

    # Create output image with the target dimensions (with transparency)
    result = np.zeros((target_height, target_width, 4), dtype=np.uint8)

    # Calculate centering offsets to place the resized image in the center
    x_offset = (target_width - new_w) // 2
    y_offset = (target_height - new_h) // 2

    # Place the resized image into the center of the result canvas
    result[y_offset:y_offset+new_h, x_offset:x_offset+new_w, :3] = foreground_bgr
    result[y_offset:y_offset+new_h, x_offset:x_offset+new_w, 3] = foreground_alpha

    return result

# Apply the resize
resized_image = resize_contain(image, target_width, target_height)

# Save the result image (PNG format to preserve transparency)
cv2.imwrite('./images/logo_42_42.jpg', resized_image)

cv2.destroyAllWindows()
